// The assessment uses one seeded investor for demonstration purposes.
const INVESTOR_ID = 1;

let currentPortfolio = null;
// Cache frequently used HTML elements.
const portfolioLoading = document.getElementById("portfolio-loading");
const portfolioContent = document.getElementById("portfolio-content");

const investorName = document.getElementById("investor-name");
const investorAge = document.getElementById("investor-age");
const portfolioBalance = document.getElementById("portfolio-balance");

const productList = document.getElementById("product-list");
const productSelect = document.getElementById("product-select");

const withdrawalForm = document.getElementById("withdrawal-form");
const withdrawalAmount = document.getElementById("withdrawal-amount");
const withdrawalButton = document.getElementById("withdrawal-button");

const withdrawalHistory =
    document.getElementById("withdrawal-history");

const message = document.getElementById("message");

const refreshPortfolioButton =
    document.getElementById("refresh-portfolio-button");

const downloadCsvButton =
    document.getElementById("download-csv-button");


/**
 * Formats numeric investment values as South African Rand.
 */
function formatCurrency(value) {

    return new Intl.NumberFormat("en-ZA", {
        style: "currency",
        currency: "ZAR"
    }).format(value);
}


/**
 * Converts API timestamps into a readable local date and time.
 */
function formatDate(value) {

    return new Date(value).toLocaleString("en-ZA");
}


/**
 * Displays success or error feedback to the user.
 */
function showMessage(text, type) {

    message.textContent = text;

    message.className = `message ${type}`;
}


/**
 * Loads portfolio information from the Spring Boot REST API.
 */
async function loadPortfolio() {

    portfolioLoading.classList.remove("hidden");
    portfolioContent.classList.add("hidden");

    try {

        const response = await fetch(
            `/api/investors/${INVESTOR_ID}/portfolio`
        );

        if (!response.ok) {
            throw new Error("Unable to load portfolio");
        }

        const portfolio = await response.json();

        currentPortfolio = portfolio;
        investorName.textContent = portfolio.investorName;
        investorAge.textContent = portfolio.age;

        portfolioBalance.textContent =
            formatCurrency(portfolio.balance);

        renderProducts(portfolio.products);

        portfolioLoading.classList.add("hidden");
        portfolioContent.classList.remove("hidden");

    } catch (error) {

        portfolioLoading.textContent =
            "Unable to load portfolio.";

        console.error(error);
    }
}


/**
 * Displays investment products and also populates the
 * withdrawal product selector.
 */
function renderProducts(products) {

    productList.innerHTML = "";

    productSelect.innerHTML =
        '<option value="">Select a product</option>';

    products.forEach(product => {

        const card = document.createElement("div");

        card.className = "product-card";

        card.innerHTML = `
            <h4>${product.name}</h4>

            <span class="product-type">
                ${product.type}
            </span>

            <span class="product-value">
                ${formatCurrency(product.productValue)}
            </span>
        `;

        productList.appendChild(card);


        const option = document.createElement("option");

        option.value = product.id;

        option.textContent =
            `${product.name} - ${formatCurrency(product.productValue)}`;

        productSelect.appendChild(option);
    });
}


/**
 * Retrieves withdrawal history from the backend.
 */
async function loadWithdrawalHistory() {

    try {

        const response = await fetch(
            `/api/withdrawals?investorId=${INVESTOR_ID}`
        );

        if (!response.ok) {
            throw new Error("Unable to load withdrawal history");
        }

        const withdrawals = await response.json();

        renderWithdrawalHistory(withdrawals);

    } catch (error) {

        withdrawalHistory.innerHTML = `
            <tr>
                <td colspan="5">
                    Unable to load withdrawal history.
                </td>
            </tr>
        `;

        console.error(error);
    }
}


/**
 * Converts withdrawal history returned by the API
 * into table rows.
 */
function renderWithdrawalHistory(withdrawals) {

    withdrawalHistory.innerHTML = "";

    if (withdrawals.length === 0) {

        withdrawalHistory.innerHTML = `
            <tr>
                <td colspan="5">
                    No withdrawals recorded.
                </td>
            </tr>
        `;

        return;
    }

    withdrawals.forEach(withdrawal => {

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${withdrawal.withdrawalId}</td>

            <td>${withdrawal.productName}</td>

            <td>
                ${formatCurrency(withdrawal.amount)}
            </td>

            <td>
                <span class="status-badge">
                    ${withdrawal.status}
                </span>
            </td>

            <td>
                ${formatDate(withdrawal.createdAt)}
            </td>
        `;

        withdrawalHistory.appendChild(row);
    });
}


/**
 * Sends a withdrawal request to Spring Boot.
 */
async function submitWithdrawal(event) {

    // Prevent the browser from performing a normal form submission.
    event.preventDefault();

    const productId = productSelect.value;
    const amount = withdrawalAmount.value;

    /*
     * Basic client-side validation happens first.
     * The backend still performs authoritative validation.
     */
    if (!productId) {

        showMessage(
            "Please select an investment product.",
            "error"
        );

        return;
    }

    if (!amount || Number(amount) <= 0) {

        showMessage(
            "Withdrawal amount must be greater than zero.",
            "error"
        );

        return;
    }

    /*
     * Find the selected product from the portfolio already
     * loaded from the backend.
     */
    const selectedProduct = currentPortfolio?.products.find(
        product => product.id === Number(productId)
    );

    if (!selectedProduct) {

        showMessage(
            "Unable to find the selected investment product.",
            "error"
        );

        return;
    }

    const requestedAmount = Number(amount);

    const maximumWithdrawal =
        Number(selectedProduct.productValue) * 0.90;

    /*
     * Give immediate feedback if the request exceeds
     * the 90% withdrawal limit.
     */
    if (requestedAmount > maximumWithdrawal) {

        showMessage(
            `Maximum withdrawal for this product is ${formatCurrency(
                maximumWithdrawal
            )}.`,
            "error"
        );

        return;
    }

    /*
     * Retirement products require the investor
     * to be older than 65.
     */
    if (
        selectedProduct.type === "RETIREMENT"
        && Number(currentPortfolio.age) <= 65
    ) {

        showMessage(
            "Retirement withdrawals are only available to investors older than 65.",
            "error"
        );

        return;
    }

    const request = {
        investorId: INVESTOR_ID,
        productId: Number(productId),
        amount: requestedAmount
    };

    withdrawalButton.disabled = true;
    withdrawalButton.textContent = "Processing...";

    try {

        const response = await fetch(
            "/api/withdrawals",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(request)
            }
        );

        const result = await response.json();

        if (!response.ok) {

            showMessage(
                result.message || "Withdrawal failed.",
                "error"
            );

            return;
        }

        showMessage(
            `Withdrawal approved. Remaining portfolio balance: ${formatCurrency(
                result.remainingPortfolioBalance
            )}`,
            "success"
        );

        withdrawalForm.reset();

        // Refresh state affected by the withdrawal.
        await loadPortfolio();
        await loadWithdrawalHistory();

    } catch (error) {

        showMessage(
            "Unable to connect to the withdrawal service.",
            "error"
        );

        console.error(error);

    } finally {

        withdrawalButton.disabled = false;
        withdrawalButton.textContent =
            "Submit Withdrawal";
    }
}