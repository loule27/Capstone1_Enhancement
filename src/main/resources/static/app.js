const API = "http://localhost:8080/api/transactions";

// SCREEN NAVIGATION

function showScreen(name) {
    document.querySelectorAll(".screen").forEach(
        s => s.classList.remove("active")
    );
    document.querySelectorAll(".nav button").forEach(
        b => b.classList.remove("active")
    );

    document.getElementById("screen-" + name).classList.add("active");

    var buttons = document.querySelectorAll(".nav button");
    for (var i = 0; i < buttons.length; i++) {
        if (buttons[i].textContent.toLowerCase() === name) {
            buttons[i].classList.add("active");
        }
    }

    if (name === "home") {
        loadHomeTransactions();
        loadSummary();
    }
    if (name === "ledger") {
        filterLedger("all", document.querySelector(".filter-bar .btn-outline"));
    }
}

// HOME SCREEN

function submitDeposit() {
    var desc = document.getElementById("dep-desc").value.trim();
    var vendor = document.getElementById("dep-vendor").value.trim();
    var amount = parseFloat(document.getElementById("dep-amount").value);

    if (!desc || !vendor || isNaN(amount) || amount <= 0) {
        alert("Please fill in all fields with a valid positive amount.");
        return;
    }

    var body = {
        type: "DEPOSIT",
        description: desc,
        vendor: vendor,
        amount: amount
    };

    fetch(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
        .then(function(res) {
            if (!res.ok) throw new Error("Error creating deposit");
            return res.json();
        })
        .then(function() {
            document.getElementById("dep-desc").value = "";
            document.getElementById("dep-vendor").value = "";
            document.getElementById("dep-amount").value = "";
            loadHomeTransactions();
            loadSummary();
        })
        .catch(function(err) { alert(err.message); });
}

function submitPayment() {
    var desc = document.getElementById("pay-desc").value.trim();
    var vendor = document.getElementById("pay-vendor").value.trim();
    var amount = parseFloat(document.getElementById("pay-amount").value);

    if (!desc || !vendor || isNaN(amount) || amount <= 0) {
        alert("Please fill in all fields with a valid positive amount.");
        return;
    }

    var body = {
        type: "DEBIT",
        description: desc,
        vendor: vendor,
        amount: amount
    };

    fetch(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
        .then(function(res) {
            if (!res.ok) throw new Error("Error creating payment");
            return res.json();
        })
        .then(function() {
            document.getElementById("pay-desc").value = "";
            document.getElementById("pay-vendor").value = "";
            document.getElementById("pay-amount").value = "";
            loadHomeTransactions();
            loadSummary();
        })
        .catch(function(err) { alert(err.message); });
}

function loadHomeTransactions() {
    fetch(API)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            renderTable(data, "home-transactions");
        });
}

function loadSummary() {
    fetch(API + "/summary")
        .then(function(res) { return res.json(); })
        .then(function(data) {
            document.getElementById("sum-deposits").textContent =
                "$" + data.totalDeposits.toFixed(2);
            document.getElementById("sum-payments").textContent =
                "$" + data.totalPayments.toFixed(2);
            document.getElementById("sum-balance").textContent =
                "$" + data.balance.toFixed(2);
        });
}

// LEDGER SCREEN

function filterLedger(filter, btn) {
    document.querySelectorAll(".filter-bar .btn-outline").forEach(
        function(b) { b.classList.remove("active"); }
    );
    if (btn) btn.classList.add("active");

    document.getElementById("vendor-search").value = "";

    var url;
    if (filter === "deposits") {
        url = API + "/deposits";
    } else if (filter === "payments") {
        url = API + "/payments";
    } else {
        url = API;
    }

    fetch(url)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            renderTable(data, "ledger-transactions");
            updateLedgerSummary(data);
        });
}

function searchVendor() {
    var query = document.getElementById("vendor-search").value.trim();

    document.querySelectorAll(".filter-bar .btn-outline").forEach(
        function(b) { b.classList.remove("active"); }
    );

    if (query === "") {
        filterLedger("all", document.querySelector(".filter-bar .btn-outline"));
        return;
    }

    fetch(API + "/vendor?name=" + encodeURIComponent(query))
        .then(function(res) { return res.json(); })
        .then(function(data) {
            renderTable(data, "ledger-transactions");
            updateLedgerSummary(data);
        });
}

function updateLedgerSummary(transactions) {
    var totalDeposits = 0;
    var totalPayments = 0;

    for (var i = 0; i < transactions.length; i++) {
        if (transactions[i].type === "DEPOSIT") {
            totalDeposits += transactions[i].amount;
        } else {
            totalPayments += transactions[i].amount;
        }
    }

    var balance = totalDeposits - totalPayments;

    document.getElementById("ledger-deposits").textContent =
        "$" + totalDeposits.toFixed(2);
    document.getElementById("ledger-payments").textContent =
        "$" + totalPayments.toFixed(2);
    document.getElementById("ledger-balance").textContent =
        "$" + balance.toFixed(2);
}

// REPORTS SCREEN

function runReport(reportName) {
    fetch(API + "/reports/" + reportName)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            displayReportResults(reportName, data);
        });
}

function runCustomSearch() {
    var startDate = document.getElementById("search-start").value;
    var endDate = document.getElementById("search-end").value;
    var description = document.getElementById("search-desc").value.trim();
    var vendor = document.getElementById("search-vendor").value.trim();
    var type = document.getElementById("search-type").value;

    var params = [];
    if (startDate) params.push("startDate=" + startDate);
    if (endDate) params.push("endDate=" + endDate);
    if (description) params.push("description=" + encodeURIComponent(description));
    if (vendor) params.push("vendor=" + encodeURIComponent(vendor));
    if (type) params.push("type=" + type);

    var queryString = params.length > 0 ? "?" + params.join("&") : "";

    fetch(API + "/search" + queryString)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            displayReportResults("Custom search", data);
        });
}

function displayReportResults(title, data) {
    var container = document.getElementById("report-results");

    var totalDeposits = 0;
    var totalPayments = 0;

    for (var i = 0; i < data.length; i++) {
        if (data[i].type === "DEPOSIT") {
            totalDeposits += data[i].amount;
        } else {
            totalPayments += data[i].amount;
        }
    }

    var balance = totalDeposits - totalPayments;
    var reportTitle = title.replace(/-/g, " ");
    reportTitle = reportTitle.charAt(0).toUpperCase() + reportTitle.slice(1);

    var html = "";

    html += '<div class="summary-bar">';
    html += '<div class="summary-item"><div class="label">Deposits</div>';
    html += '<div class="value deposit">$' + totalDeposits.toFixed(2) + '</div></div>';
    html += '<div class="summary-item"><div class="label">Payments</div>';
    html += '<div class="value payment">$' + totalPayments.toFixed(2) + '</div></div>';
    html += '<div class="summary-item"><div class="label">Balance</div>';
    html += '<div class="value balance">$' + balance.toFixed(2) + '</div></div>';
    html += '</div>';

    html += '<div class="card">';
    html += '<h3>' + reportTitle + ' (' + data.length + ' transactions)</h3>';

    if (data.length === 0) {
        html += '<div class="empty-state">No transactions found.</div>';
    } else {
        html += '<div class="table-wrapper"><table>';
        html += '<thead><tr>';
        html += '<th>Date</th><th>Time</th><th>Type</th>';
        html += '<th>Description</th><th>Vendor</th><th>Amount</th>';
        html += '</tr></thead><tbody>';

        for (var j = 0; j < data.length; j++) {
            html += buildTableRow(data[j], false);
        }

        html += '</tbody></table></div>';
    }

    html += '</div>';

    container.innerHTML = html;
}

// SHARED TABLE RENDERING

function renderTable(data, tableBodyId) {
    var tbody = document.getElementById(tableBodyId);
    tbody.innerHTML = "";

    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">' +
            'No transactions found.</td></tr>';
        return;
    }

    for (var i = 0; i < data.length; i++) {
        tbody.innerHTML += buildTableRow(data[i], true);
    }
}

function buildTableRow(t, showDelete) {
    var timestamp = t.timestamp;
    var date = timestamp.substring(0, 10);
    var time = new Date(timestamp).toLocaleTimeString("en-US", {
        hour: "numeric",
        minute: "2-digit",
        hour12: true
    });

    var typeBadge;
    if (t.type === "DEPOSIT") {
        typeBadge = '<span class="type-badge type-deposit">Deposit</span>';
    } else {
        typeBadge = '<span class="type-badge type-debit">Debit</span>';
    }

    var amountClass;
    if (t.type === "DEPOSIT") {
        amountClass = "amount-deposit";
    } else {
        amountClass = "amount-payment";
    }

    var amountPrefix;
    if (t.type === "DEPOSIT") {
        amountPrefix = "+$";
    } else {
        amountPrefix = "-$";
    }

    var deleteCell = "";
    if (showDelete) {
        deleteCell = '<td><button class="delete-btn" ' +
            'onclick="deleteTransaction(' + t.id + ')">&times;</button></td>';
    }

    return '<tr>' +
        '<td>' + date + '</td>' +
        '<td>' + time + '</td>' +
        '<td>' + typeBadge + '</td>' +
        '<td>' + t.description + '</td>' +
        '<td>' + t.vendor + '</td>' +
        '<td class="' + amountClass + '">' + amountPrefix + t.amount.toFixed(2) + '</td>' +
        deleteCell + '</tr>';
}

// DELETE

function deleteTransaction(id) {
    if (!confirm("Delete this transaction?")) return;

    fetch(API + "/" + id, { method: "DELETE" })
        .then(function(res) {
            if (!res.ok) throw new Error("Error deleting transaction");
            loadHomeTransactions();
            loadSummary();

            var ledgerScreen = document.getElementById("screen-ledger");
            if (ledgerScreen.classList.contains("active")) {
                filterLedger("all",
                    document.querySelector(".filter-bar .btn-outline"));
            }
        })
        .catch(function(err) { alert(err.message); });
}

// STARTUP

window.onload = function() {
    loadHomeTransactions();
    loadSummary();
};