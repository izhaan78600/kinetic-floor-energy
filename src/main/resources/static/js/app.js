// Smart Urban Subway Kinetic Floor Energy Harvesting System - Frontend Controller
// Clean, Minimal, Modern, Professional JavaScript

let currentUser = null; // { id, name, email, role: 'USER' | 'ADMIN' }
let currentAdminTab = 'dashboard';
let currentReportSubtab = 'pnl';

// Global Data Caches
let sectorsCache = [];
let customersCache = [];
let vendorsCache = [];

// Chart References
let dailyEnergyChart = null;
let sectorShareChart = null;

// ==========================================
// INITIALIZATION
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    // Restore session if available
    const saved = sessionStorage.getItem('metro_kinetic_session');
    if (saved) {
        try {
            currentUser = JSON.parse(saved);
        } catch (e) {
            currentUser = null;
        }
    }

    // Set today's date in form inputs
    const today = new Date().toISOString().split('T')[0];
    ['telem-input-date', 'sale-input-date', 'bill-input-date', 'pay-input-date'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = today;
    });

    renderAuthWidget();
    routeView();
});

// ==========================================
// TOAST NOTIFICATIONS & MODALS
// ==========================================
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    const isSuccess = type === 'success';
    const isError = type === 'error';

    const bgClass = isSuccess ? 'bg-green-50 border-green-200 text-green-800' :
                   isError ? 'bg-red-50 border-red-200 text-red-800' :
                   'bg-blue-50 border-blue-200 text-blue-800';
    const icon = isSuccess ? 'fa-circle-check text-green-600' :
                 isError ? 'fa-circle-exclamation text-red-600' :
                 'fa-circle-info text-blue-600';

    toast.className = `p-3 rounded-md border shadow-md flex items-center space-x-2.5 text-xs max-w-sm pointer-events-auto bg-white transition-all duration-300 transform translate-y-2 opacity-0 ${bgClass}`;
    toast.innerHTML = `<i class="fa-solid ${icon} text-sm"></i><div class="flex-1 font-medium">${message}</div>`;

    container.appendChild(toast);
    setTimeout(() => toast.classList.remove('translate-y-2', 'opacity-0'), 10);
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-2');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('hidden');
        populateModalDropdowns(id);
    }
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('hidden');
}

function populateModalDropdowns(modalId) {
    if (modalId === 'modal-telemetry') {
        const sel = document.getElementById('telem-input-sector');
        sel.innerHTML = sectorsCache.map(s => `<option value="${s.id}">${s.stationName} - ${s.sectorName}</option>`).join('');
    } else if (modalId === 'modal-sale') {
        const sel = document.getElementById('sale-input-customer');
        sel.innerHTML = customersCache.map(c => `<option value="${c.id}">${c.customerName} (${c.contact || c.email})</option>`).join('');
        calcSaleTotal();
    } else if (modalId === 'modal-bill') {
        const sel = document.getElementById('bill-input-vendor');
        sel.innerHTML = vendorsCache.map(v => `<option value="${v.id}">${v.vendorName}</option>`).join('');
    } else if (modalId === 'modal-budget') {
        const sel = document.getElementById('bgt-input-sector');
        sel.innerHTML = sectorsCache.map(s => `<option value="${s.id}">${s.stationName} - ${s.sectorName}</option>`).join('');
    }
}

// ==========================================
// VIEW ROUTING & AUTH STATE
// ==========================================
function routeView() {
    const authView = document.getElementById('view-auth');
    const userView = document.getElementById('view-user-portal');
    const adminView = document.getElementById('view-admin-panel');

    authView.classList.add('hidden');
    userView.classList.add('hidden');
    adminView.classList.add('hidden');

    if (!currentUser) {
        authView.classList.remove('hidden');
    } else if (currentUser.role === 'ADMIN') {
        adminView.classList.remove('hidden');
        loadAllData();
        switchAdminTab(currentAdminTab);
    } else {
        userView.classList.remove('hidden');
        document.getElementById('user-portal-name').innerText = currentUser.name || 'Operator';
        loadUserPortalData();
    }
}

function renderAuthWidget() {
    const widget = document.getElementById('auth-status-widget');
    if (!widget) return;

    if (!currentUser) {
        widget.innerHTML = `<button onclick="routeView()" class="btn-primary text-xs py-1.5 px-3">Sign In</button>`;
    } else {
        const roleBadge = currentUser.role === 'ADMIN' ?
            `<span class="badge-tag badge-info">Admin</span>` :
            `<span class="badge-tag badge-success">User</span>`;

        widget.innerHTML = `
            <div class="flex items-center space-x-2 text-xs">
                ${roleBadge}
                <span class="text-slate-700 font-semibold hidden sm:inline">${currentUser.name}</span>
                <button onclick="handleLogout()" title="Logout" class="px-2 py-1 text-xs text-red-600 hover:text-red-800 font-medium">
                    <i class="fa-solid fa-arrow-right-from-bracket mr-1"></i>Logout
                </button>
            </div>
        `;
    }
}

function handleLogout() {
    currentUser = null;
    sessionStorage.removeItem('metro_kinetic_session');
    renderAuthWidget();
    routeView();
    showToast("Logged out successfully", "info");
}

function switchAuthMode(mode) {
    ['user-login', 'user-signup', 'admin-login'].forEach(m => {
        const btn = document.getElementById(`btn-mode-${m}`);
        const form = document.getElementById(`form-${m}`);
        if (!btn || !form) return;
        if (m === mode) {
            btn.className = "py-1.5 rounded bg-white text-blue-600 font-semibold shadow-sm";
            form.classList.remove('hidden');
        } else {
            btn.className = "py-1.5 rounded text-slate-600 hover:text-slate-900";
            form.classList.add('hidden');
        }
    });
}

function quickFillAdmin() {
    switchAuthMode('admin-login');
    document.getElementById('admin-login-email').value = 'admin@metro.in';
    document.getElementById('admin-login-pass').value = 'admin123';
}

function quickFillUser() {
    switchAuthMode('user-login');
    document.getElementById('user-login-email').value = 'user@metro.in';
    document.getElementById('user-login-pass').value = 'user123';
}

// ==========================================
// AUTHENTICATION HANDLERS
// ==========================================
async function handleUserLogin(e) {
    e.preventDefault();
    const email = document.getElementById('user-login-email').value;
    const password = document.getElementById('user-login-pass').value;

    try {
        const res = await fetch('/api/auth/user/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok) {
            currentUser = { id: data.userId, name: data.name, email: data.email, role: 'USER' };
            sessionStorage.setItem('metro_kinetic_session', JSON.stringify(currentUser));
            renderAuthWidget();
            routeView();
            showToast(`Welcome back, ${data.name}!`);
        } else {
            showToast(data.error || 'User login failed', 'error');
        }
    } catch (err) {
        showToast('Server connection error. Make sure Spring Boot is running.', 'error');
    }
}

async function handleUserSignUp(e) {
    e.preventDefault();
    const name = document.getElementById('user-reg-name').value;
    const email = document.getElementById('user-reg-email').value;
    const password = document.getElementById('user-reg-pass').value;

    try {
        const res = await fetch('/api/auth/user/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        const data = await res.json();
        if (res.ok) {
            currentUser = { id: data.userId, name: data.name, email: data.email, role: 'USER' };
            sessionStorage.setItem('metro_kinetic_session', JSON.stringify(currentUser));
            renderAuthWidget();
            routeView();
            showToast(`Account created! Welcome, ${data.name}`);
        } else {
            showToast(data.error || 'User registration failed', 'error');
        }
    } catch (err) {
        showToast('Server connection error.', 'error');
    }
}

async function handleAdminLogin(e) {
    e.preventDefault();
    const email = document.getElementById('admin-login-email').value;
    const password = document.getElementById('admin-login-pass').value;

    try {
        const res = await fetch('/api/auth/admin/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok) {
            currentUser = { id: data.adminId, name: data.name, email: data.email, role: 'ADMIN' };
            sessionStorage.setItem('metro_kinetic_session', JSON.stringify(currentUser));
            renderAuthWidget();
            routeView();
            showToast(`Administrator Authenticated: ${data.name}`);
        } else {
            showToast(data.error || 'Admin authentication failed', 'error');
        }
    } catch (err) {
        showToast('Server connection error. Make sure Spring Boot is running.', 'error');
    }
}

// ==========================================
// ADMIN TABS NAVIGATION
// ==========================================
function switchAdminTab(tabName) {
    currentAdminTab = tabName;

    document.querySelectorAll('.sidebar-link').forEach(b => b.classList.remove('active', 'nav-tab-active'));
    const btn = document.getElementById(`nav-tab-${tabName}`);
    if (btn) btn.classList.add('active', 'nav-tab-active');

    document.querySelectorAll('.admin-tab-view').forEach(v => v.classList.add('hidden'));
    const activeView = document.getElementById(`admin-tab-${tabName}`);
    if (activeView) activeView.classList.remove('hidden');

    if (tabName === 'dashboard') loadDashboard();
    else if (tabName === 'telemetry') loadTelemetry();
    else if (tabName === 'sectors') loadSectors();
    else if (tabName === 'tiles') loadTiles();
    else if (tabName === 'sales') loadSalesAndInvoices();
    else if (tabName === 'bills') loadVendorBills();
    else if (tabName === 'payments') loadPayments();
    else if (tabName === 'budgets') loadBudgets();
    else if (tabName === 'parties') loadParties();
    else if (tabName === 'users') loadUsersAndAdmins();
    else if (tabName === 'accounting') loadAccounting();
    else if (tabName === 'reports') switchReportSubtab(currentReportSubtab);
}

function switchReportSubtab(subtab) {
    currentReportSubtab = subtab;
    const subtabs = ['pnl', 'budget', 'energy', 'invoices', 'bills'];

    subtabs.forEach(st => {
        const btn = document.getElementById(`rep-btn-${st}`);
        const view = document.getElementById(`rep-view-${st}`);
        if (st === subtab) {
            if (btn) btn.className = "btn-primary text-xs py-1.5 px-3";
            if (view) view.classList.remove('hidden');
        } else {
            if (btn) btn.className = "btn-secondary text-xs py-1.5 px-3";
            if (view) view.classList.add('hidden');
        }
    });

    if (subtab === 'pnl') loadPnLReport();
    else if (subtab === 'budget') loadBudgetReport();
    else if (subtab === 'energy') loadEnergyReport();
    else if (subtab === 'invoices') loadInvoiceReport();
    else if (subtab === 'bills') loadVendorBillReport();
}

// ==========================================
// PRELOAD ALL DATA & CACHES
// ==========================================
async function loadAllData() {
    try {
        const [secRes, custRes, venRes] = await Promise.all([
            fetch('/api/kinetic/sectors'),
            fetch('/api/customers'),
            fetch('/api/vendors')
        ]);
        sectorsCache = await secRes.json();
        customersCache = await custRes.json();
        vendorsCache = await venRes.json();
    } catch (err) {
        console.error("Error preloading data:", err);
    }
}

// ==========================================
// DASHBOARD CONTROLLER
// ==========================================
async function loadDashboard() {
    try {
        const res = await fetch('/api/dashboard/stats');
        const data = await res.json();

        // Cards (Amounts in INR ₹)
        document.getElementById('dash-stat-steps').innerText = Number(data.totalSteps).toLocaleString();
        document.getElementById('dash-stat-gen-kwh').innerText = Number(data.totalEnergyGeneratedKwh).toFixed(2);
        document.getElementById('dash-stat-sold-kwh').innerText = Number(data.totalEnergySoldKwh).toFixed(2);
        document.getElementById('dash-stat-revenue').innerText = Number(data.totalRevenueInr).toLocaleString('en-IN', { minimumFractionDigits: 2 });
        document.getElementById('dash-stat-expenses').innerText = Number(data.totalExpensesInr).toLocaleString('en-IN', { minimumFractionDigits: 2 });
        document.getElementById('dash-stat-profit').innerText = Number(data.netProfitInr).toLocaleString('en-IN', { minimumFractionDigits: 2 });

        // Double-entry Liquidity
        document.getElementById('dash-stat-bank').innerText = Number(data.bankBalanceInr).toLocaleString('en-IN', { minimumFractionDigits: 2 });
        document.getElementById('dash-stat-receivable').innerText = Number(data.customerReceivableInr).toLocaleString('en-IN', { minimumFractionDigits: 2 });
        document.getElementById('dash-stat-payable').innerText = Number(data.vendorPayableInr).toLocaleString('en-IN', { minimumFractionDigits: 2 });

        renderDashboardCharts(data.dailyGenerationChart, data.sectorGenerationChart);
    } catch (err) {
        console.error("Dashboard load error:", err);
    }
}

function renderDashboardCharts(dailyChartData, sectorChartData) {
    // 1. Daily Generation Bar & Line Combo Chart
    const dailyCtx = document.getElementById('chart-daily-energy');
    if (dailyCtx && dailyChartData) {
        const labels = dailyChartData.map(d => d.date);
        const kwh = dailyChartData.map(d => d.kwh);
        const steps = dailyChartData.map(d => (d.steps / 1000).toFixed(1));

        if (dailyEnergyChart) dailyEnergyChart.destroy();

        dailyEnergyChart = new Chart(dailyCtx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Energy (kWh)',
                        data: kwh,
                        backgroundColor: '#2563eb',
                        borderColor: '#1d4ed8',
                        borderWidth: 1,
                        borderRadius: 3,
                        yAxisID: 'y'
                    },
                    {
                        type: 'line',
                        label: 'Footsteps (x1,000)',
                        data: steps,
                        borderColor: '#16a34a',
                        backgroundColor: 'rgba(22, 163, 74, 0.08)',
                        borderWidth: 2,
                        tension: 0.2,
                        yAxisID: 'y1'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                        labels: { boxWidth: 12, font: { size: 11, family: 'Inter' }, color: '#475569' }
                    }
                },
                scales: {
                    x: { grid: { color: '#f1f5f9' }, ticks: { color: '#64748b', font: { size: 10, family: 'Inter' } } },
                    y: { grid: { color: '#e2e8f0' }, ticks: { color: '#2563eb', font: { size: 10, family: 'Inter' } } },
                    y1: { position: 'right', grid: { drawOnChartArea: false }, ticks: { color: '#16a34a', font: { size: 10, family: 'Inter' } } }
                }
            }
        });
    }

    // 2. Sector Share Donut Chart
    const sectorCtx = document.getElementById('chart-sector-share');
    if (sectorCtx && sectorChartData) {
        const labels = sectorChartData.map(s => s.sectorName);
        const kwh = sectorChartData.map(s => s.kwh);

        if (sectorShareChart) sectorShareChart.destroy();

        sectorShareChart = new Chart(sectorCtx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: kwh,
                    backgroundColor: ['#2563eb', '#16a34a', '#d97706', '#8b5cf6', '#0ea5e9'],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { color: '#475569', boxWidth: 10, font: { size: 11, family: 'Inter' } }
                    }
                },
                cutout: '60%'
            }
        });
    }
}

// ==========================================
// TELEMETRY CONTROLLER
// ==========================================
async function loadTelemetry() {
    try {
        const res = await fetch('/api/kinetic/telemetry');
        const telemetry = await res.json();
        const tbody = document.getElementById('admin-telemetry-body');

        if (!telemetry || telemetry.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-slate-500 py-6">No telemetry records found</td></tr>`;
            return;
        }

        tbody.innerHTML = telemetry.map(t => `
            <tr>
                <td class="font-medium text-slate-800">#${t.id}</td>
                <td>${t.sector ? t.sector.stationName + ' - ' + t.sector.sectorName : 'Sector #' + t.sectorId}</td>
                <td class="text-slate-600">${t.date}</td>
                <td class="text-slate-900 font-semibold">${Number(t.stepCount).toLocaleString()}</td>
                <td class="text-blue-700 font-bold">${Number(t.energyKwh).toFixed(3)} kWh</td>
            </tr>
        `).join('');
    } catch (err) {
        console.error("Telemetry load error:", err);
    }
}

function autoCalcTelemEnergy(force = false) {
    const steps = parseInt(document.getElementById('telem-input-steps').value || 0);
    const kwhInput = document.getElementById('telem-input-kwh');
    if (steps > 0 && (force || !kwhInput.value)) {
        kwhInput.value = (steps * 0.00026).toFixed(3);
    }
}

async function submitTelemetry(e) {
    e.preventDefault();
    const sectorId = document.getElementById('telem-input-sector').value;
    const date = document.getElementById('telem-input-date').value;
    const steps = document.getElementById('telem-input-steps').value;
    const kwh = document.getElementById('telem-input-kwh').value;

    try {
        const res = await fetch('/api/kinetic/telemetry', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sectorId, date, stepCount: steps, energyKwh: kwh })
        });
        const data = await res.json();
        if (res.ok) {
            showToast(`Telemetry logged: ${Number(data.stepCount).toLocaleString()} steps (${data.energyKwh} kWh)`);
            closeModal('modal-telemetry');
            e.target.reset();
            if (currentUser && currentUser.role === 'ADMIN') {
                loadTelemetry();
                loadDashboard();
            } else {
                loadUserPortalData();
            }
        } else {
            showToast(data.error || 'Failed to save telemetry', 'error');
        }
    } catch (err) {
        showToast('Error saving telemetry', 'error');
    }
}

async function triggerSimulateBurst() {
    try {
        const res = await fetch('/api/kinetic/telemetry/simulate', { method: 'POST' });
        const data = await res.json();
        if (res.ok) {
            showToast(data.message);
            if (currentUser && currentUser.role === 'ADMIN') {
                loadDashboard();
                if (currentAdminTab === 'telemetry') loadTelemetry();
            } else if (currentUser) {
                loadUserPortalData();
            }
        } else {
            showToast(data.error || 'Simulation failed', 'error');
        }
    } catch (err) {
        showToast('Error connecting to backend simulation', 'error');
    }
}

// ==========================================
// USER PORTAL CONTROLLER
// ==========================================
async function loadUserPortalData() {
    try {
        const [statsRes, telemRes, secRes] = await Promise.all([
            fetch('/api/kinetic/stats'),
            fetch('/api/kinetic/telemetry'),
            fetch('/api/kinetic/sectors')
        ]);
        const stats = await statsRes.json();
        const telemetry = await telemRes.json();
        sectorsCache = await secRes.json();

        const stepsEl = document.getElementById('user-stat-steps') || document.getElementById('user-kpi-steps');
        const kwhEl = document.getElementById('user-stat-kwh') || document.getElementById('user-kpi-energy');
        const secEl = document.getElementById('user-stat-sectors') || document.getElementById('user-kpi-sectors');

        if (stepsEl) stepsEl.innerText = Number(stats.totalSteps || 0).toLocaleString();
        if (kwhEl) kwhEl.innerText = `${Number(stats.totalEnergyKwh || 0).toFixed(2)} kWh`;
        if (secEl) secEl.innerText = sectorsCache.length || 4;

        const tbody = document.getElementById('user-telemetry-body') || document.getElementById('user-telemetry-table-body');
        if (!tbody) return;

        if (!telemetry || telemetry.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-slate-500 py-6">No footstep records logged yet</td></tr>`;
            return;
        }

        tbody.innerHTML = telemetry.slice(0, 10).map(t => `
            <tr>
                <td class="font-medium text-slate-800">#${t.id}</td>
                <td class="font-medium">${t.sector ? t.sector.stationName + ' - ' + t.sector.sectorName : 'Sector #' + t.sectorId}</td>
                <td class="text-slate-600">${t.date}</td>
                <td class="text-slate-900 font-semibold">${Number(t.stepCount).toLocaleString()}</td>
                <td class="text-blue-700 font-bold">${Number(t.energyKwh).toFixed(3)} kWh</td>
            </tr>
        `).join('');
    } catch (err) {
        console.error("User portal data error:", err);
    }
}

// ==========================================
// SECTORS CONTROLLER
// ==========================================
async function loadSectors() {
    try {
        const res = await fetch('/api/kinetic/sectors');
        sectorsCache = await res.json();
        const tbody = document.getElementById('admin-sectors-body');

        tbody.innerHTML = sectorsCache.map(s => `
            <tr>
                <td class="font-medium text-slate-800">#${s.id}</td>
                <td class="font-semibold text-slate-900">${s.stationName}</td>
                <td class="text-blue-700 font-medium">${s.sectorName}</td>
                <td class="text-slate-600">${s.location || '-'}</td>
                <td class="font-bold text-slate-900">${s.tileCount} Tiles</td>
                <td>
                    <button onclick="deleteSector(${s.id})" class="text-xs text-red-600 hover:text-red-800 font-medium">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (err) { console.error(err); }
}

async function submitSector(e) {
    e.preventDefault();
    const stationName = document.getElementById('sec-input-station').value;
    const sectorName = document.getElementById('sec-input-sector').value;
    const location = document.getElementById('sec-input-loc').value;
    const tileCount = parseInt(document.getElementById('sec-input-count').value);

    try {
        const res = await fetch('/api/kinetic/sectors', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ stationName, sectorName, location, tileCount })
        });
        if (res.ok) {
            showToast("Station Sector registered successfully!");
            closeModal('modal-sector');
            e.target.reset();
            await loadAllData();
            loadSectors();
        }
    } catch (err) { showToast("Error saving sector", "error"); }
}

async function deleteSector(id) {
    if (!confirm("Are you sure you want to remove this station sector?")) return;
    const res = await fetch(`/api/kinetic/sectors/${id}`, { method: 'DELETE' });
    if (res.ok) {
        showToast("Sector removed");
        await loadAllData();
        loadSectors();
    }
}

// ==========================================
// TILES CONTROLLER
// ==========================================
async function loadTiles() {
    try {
        const res = await fetch('/api/tiles');
        const tiles = await res.json();
        const tbody = document.getElementById('admin-tiles-body');

        tbody.innerHTML = tiles.map(t => {
            const statusClass = t.status === 'Active' ? 'badge-success' : 'badge-warning';
            return `
                <tr>
                    <td class="font-medium text-slate-800">#${t.id}</td>
                    <td class="font-semibold text-slate-900">${t.tileName}</td>
                    <td class="text-slate-600">${t.tileType || '-'}</td>
                    <td class="font-bold text-slate-900">₹${Number(t.priceInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td class="font-semibold text-slate-800">${t.quantity}</td>
                    <td><span class="badge-tag ${statusClass}">${t.status}</span></td>
                    <td>
                        <button onclick="deleteTile(${t.id})" class="text-xs text-red-600 hover:text-red-800 font-medium">Delete</button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) { console.error(err); }
}

async function submitTile(e) {
    e.preventDefault();
    const tileName = document.getElementById('tile-input-name').value;
    const tileType = document.getElementById('tile-input-type').value;
    const priceInr = parseFloat(document.getElementById('tile-input-price').value);
    const quantity = parseInt(document.getElementById('tile-input-qty').value);
    const status = document.getElementById('tile-input-status').value;

    const res = await fetch('/api/tiles', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ tileName, tileType, priceInr, quantity, status })
    });
    if (res.ok) {
        showToast("Kinetic Tile cataloged!");
        closeModal('modal-tile');
        e.target.reset();
        loadTiles();
    }
}

async function deleteTile(id) {
    if (!confirm("Are you sure?")) return;
    const res = await fetch(`/api/tiles/${id}`, { method: 'DELETE' });
    if (res.ok) {
        showToast("Tile removed");
        loadTiles();
    }
}

// ==========================================
// ENERGY SALES & CUSTOMER INVOICES
// ==========================================
async function loadSalesAndInvoices() {
    try {
        const [invRes, ordRes] = await Promise.all([
            fetch('/api/invoices'),
            fetch('/api/sales-orders')
        ]);
        const invoices = await invRes.json();
        const orders = await ordRes.json();

        // 1. Invoices Table
        const invBody = document.getElementById('admin-invoices-body');
        invBody.innerHTML = invoices.map(inv => {
            const isPaid = inv.status === 'Paid';
            const statusBadge = isPaid ?
                `<span class="badge-tag badge-success">Paid</span>` :
                `<span class="badge-tag badge-warning">Unpaid</span>`;

            const actionBtn = isPaid ?
                `<span class="text-xs text-slate-400">Settled</span>` :
                `<button onclick="collectCustomerPayment(${inv.id})" class="btn-primary text-xs py-1 px-2.5">
                    Collect Payment
                </button>`;

            const custName = inv.salesOrder && inv.salesOrder.customer ? inv.salesOrder.customer.customerName : 'Grid Customer';

            return `
                <tr>
                    <td class="font-semibold text-blue-700">INV-${inv.id}</td>
                    <td class="text-slate-600">SO-${inv.salesOrderId}</td>
                    <td class="font-medium text-slate-800">${custName}</td>
                    <td class="text-slate-600">${inv.invoiceDate}</td>
                    <td class="text-slate-600">${inv.dueDate || '-'}</td>
                    <td class="font-bold text-slate-900">₹${Number(inv.amountInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td>${statusBadge}</td>
                    <td>${actionBtn}</td>
                </tr>
            `;
        }).join('');

        // 2. Sales Orders Table
        const ordBody = document.getElementById('admin-sales-orders-body');
        ordBody.innerHTML = orders.map(ord => {
            const custName = ord.customer ? ord.customer.customerName : 'Customer #' + ord.customerId;
            return `
                <tr>
                    <td class="font-semibold text-blue-700">SO-${ord.id}</td>
                    <td class="font-medium text-slate-800">${custName}</td>
                    <td class="text-slate-600">${ord.orderDate}</td>
                    <td class="font-bold text-blue-700">${Number(ord.energyKwh).toFixed(2)} kWh</td>
                    <td class="text-slate-700">₹${Number(ord.pricePerKwh).toFixed(2)}</td>
                    <td class="font-bold text-slate-900">₹${Number(ord.totalAmountInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td><span class="badge-tag badge-info">${ord.status}</span></td>
                </tr>
            `;
        }).join('');
    } catch (err) { console.error(err); }
}

function calcSaleTotal() {
    const kwh = parseFloat(document.getElementById('sale-input-kwh').value || 0);
    const rate = parseFloat(document.getElementById('sale-input-price').value || 0);
    const total = kwh * rate;
    document.getElementById('sale-calc-total').innerText = `₹${total.toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
}

async function submitSaleOrder(e) {
    e.preventDefault();
    const customerId = document.getElementById('sale-input-customer').value;
    const orderDate = document.getElementById('sale-input-date').value;
    const energyKwh = parseFloat(document.getElementById('sale-input-kwh').value);
    const pricePerKwh = parseFloat(document.getElementById('sale-input-price').value);

    try {
        const res = await fetch('/api/sales-orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ customerId, orderDate, energyKwh, pricePerKwh })
        });
        const inv = await res.json();
        if (res.ok) {
            showToast(`Energy Sale Order created! Invoice #${inv.id} generated for ₹${inv.amountInr}`);
            closeModal('modal-sale');
            e.target.reset();
            loadSalesAndInvoices();
            loadDashboard();
        } else {
            showToast(inv.error || "Failed to create sales order", "error");
        }
    } catch (err) { showToast("Error connecting to server", "error"); }
}

function collectCustomerPayment(invoiceId) {
    document.getElementById('pay-target-type').value = 'CUSTOMER';
    document.getElementById('pay-target-id').value = invoiceId;
    document.getElementById('pay-display-label').innerText = `Settling Customer Invoice: INV-${invoiceId}`;
    openModal('modal-payment');
}

// ==========================================
// VENDOR BILLS CONTROLLER
// ==========================================
async function loadVendorBills() {
    try {
        const res = await fetch('/api/vendor-bills');
        const bills = await res.json();
        const tbody = document.getElementById('admin-bills-body');

        tbody.innerHTML = bills.map(b => {
            const isPaid = b.status === 'Paid';
            const statusBadge = isPaid ?
                `<span class="badge-tag badge-success">Paid</span>` :
                `<span class="badge-tag badge-warning">Unpaid</span>`;

            const actionBtn = isPaid ?
                `<span class="text-xs text-slate-400">Settled</span>` :
                `<button onclick="payVendorBill(${b.id})" class="btn-primary text-xs py-1 px-2.5">
                    Disburse Payment
                </button>`;

            const vName = b.vendor ? b.vendor.vendorName : 'Tile Vendor #' + b.vendorId;

            return `
                <tr>
                    <td class="font-semibold text-slate-900">BILL-${b.id}</td>
                    <td class="font-medium text-slate-800">${vName}</td>
                    <td class="text-slate-600">${b.billDate}</td>
                    <td class="text-slate-700">${b.description}</td>
                    <td class="text-slate-800 font-semibold">${b.quantity}</td>
                    <td class="font-bold text-slate-900">₹${Number(b.amountInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td>${statusBadge}</td>
                    <td>${actionBtn}</td>
                </tr>
            `;
        }).join('');
    } catch (err) { console.error(err); }
}

async function submitVendorBill(e) {
    e.preventDefault();
    const vendorId = document.getElementById('bill-input-vendor').value;
    const billDate = document.getElementById('bill-input-date').value;
    const description = document.getElementById('bill-input-desc').value;
    const quantity = parseInt(document.getElementById('bill-input-qty').value);
    const amountInr = parseFloat(document.getElementById('bill-input-amount').value);

    try {
        const res = await fetch('/api/vendor-bills', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ vendorId, billDate, description, quantity, amountInr })
        });
        const bill = await res.json();
        if (res.ok) {
            showToast(`Vendor maintenance bill #${bill.id} recorded for ₹${bill.amountInr}`);
            closeModal('modal-bill');
            e.target.reset();
            loadVendorBills();
            loadDashboard();
        }
    } catch (err) { showToast('Error posting vendor bill', 'error'); }
}

function payVendorBill(billId) {
    document.getElementById('pay-target-type').value = 'VENDOR';
    document.getElementById('pay-target-id').value = billId;
    document.getElementById('pay-display-label').innerText = `Settling Vendor Maintenance Bill: BILL-${billId}`;
    openModal('modal-payment');
}

// ==========================================
// PAYMENTS CONTROLLER
// ==========================================
async function submitPayment(e) {
    e.preventDefault();
    const type = document.getElementById('pay-target-type').value;
    const refId = document.getElementById('pay-target-id').value;
    const paymentDate = document.getElementById('pay-input-date').value;
    const paymentMethod = document.getElementById('pay-input-method').value;

    const endpoint = type === 'CUSTOMER' ? '/api/payments/customer' : '/api/payments/vendor';
    const payload = type === 'CUSTOMER' ? { invoiceId: refId, paymentDate, paymentMethod } : { billId: refId, paymentDate, paymentMethod };

    try {
        const res = await fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Payment transaction settled and journalized in MySQL!");
            closeModal('modal-payment');
            loadSalesAndInvoices();
            loadVendorBills();
            loadPayments();
            loadDashboard();
        }
    } catch (err) { showToast('Error settling payment', 'error'); }
}

async function loadPayments() {
    try {
        const res = await fetch('/api/payments');
        const payments = await res.json();
        const tbody = document.getElementById('admin-payments-body');

        tbody.innerHTML = payments.map(p => {
            const isCust = p.paymentType === 'CUSTOMER';
            const typeBadge = isCust ?
                `<span class="badge-tag badge-success">Customer Receipt</span>` :
                `<span class="badge-tag badge-info">Vendor Disbursement</span>`;

            return `
                <tr>
                    <td class="font-semibold text-blue-700">PMT-${p.id}</td>
                    <td>${typeBadge}</td>
                    <td class="text-slate-600">${isCust ? 'INV-' + p.referenceId : 'BILL-' + p.referenceId}</td>
                    <td class="text-slate-600">${p.paymentDate}</td>
                    <td class="font-bold text-slate-900">₹${Number(p.amountInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td class="text-slate-700">${p.paymentMethod}</td>
                    <td><span class="badge-tag badge-success">${p.status}</span></td>
                </tr>
            `;
        }).join('');
    } catch (err) { console.error(err); }
}

// ==========================================
// BUDGETS CONTROLLER
// ==========================================
async function loadBudgets() {
    try {
        const res = await fetch('/api/budgets');
        const budgets = await res.json();
        const tbody = document.getElementById('admin-budgets-body');

        tbody.innerHTML = budgets.map(b => {
            const revVarClass = b.revenueVarianceInr >= 0 ? 'text-green-700 font-bold' : 'text-red-700 font-bold';
            const revSign = b.revenueVarianceInr >= 0 ? '+' : '';
            const expVarClass = b.maintenanceVarianceInr >= 0 ? 'text-green-700 font-bold' : 'text-red-700 font-bold';
            const expSign = b.maintenanceVarianceInr >= 0 ? '+' : '';

            return `
                <tr>
                    <td class="font-semibold text-slate-900">${b.stationName} - ${b.sectorName}</td>
                    <td class="text-slate-600">${b.budgetYear}</td>
                    <td class="text-slate-700">₹${Number(b.plannedRevenueInr).toLocaleString('en-IN')}</td>
                    <td class="font-medium text-green-700">₹${Number(b.actualRevenueInr).toLocaleString('en-IN')}</td>
                    <td class="${revVarClass}">${revSign}₹${Number(b.revenueVarianceInr).toLocaleString('en-IN')}</td>
                    <td class="text-slate-700">₹${Number(b.plannedMaintenanceInr).toLocaleString('en-IN')}</td>
                    <td class="font-medium text-red-700">₹${Number(b.actualMaintenanceInr).toLocaleString('en-IN')}</td>
                    <td class="${expVarClass}">${expSign}₹${Number(b.maintenanceVarianceInr).toLocaleString('en-IN')}</td>
                </tr>
            `;
        }).join('');
    } catch (err) { console.error(err); }
}

async function submitBudget(e) {
    e.preventDefault();
    const sectorId = document.getElementById('bgt-input-sector').value;
    const budgetYear = document.getElementById('bgt-input-year').value;
    const plannedRevenueInr = document.getElementById('bgt-input-plan-rev').value;
    const actualRevenueInr = document.getElementById('bgt-input-act-rev').value;
    const plannedMaintenanceInr = document.getElementById('bgt-input-plan-maint').value;
    const actualMaintenanceInr = document.getElementById('bgt-input-act-maint').value;

    try {
        const res = await fetch('/api/budgets', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sectorId, budgetYear, plannedRevenueInr, actualRevenueInr, plannedMaintenanceInr, actualMaintenanceInr })
        });
        if (res.ok) {
            showToast("Station Sector Budget saved in MySQL!");
            closeModal('modal-budget');
            e.target.reset();
            loadBudgets();
        }
    } catch (err) { showToast("Error saving budget", "error"); }
}

// ==========================================
// PARTIES CONTROLLER (CUSTOMERS & VENDORS)
// ==========================================
async function loadParties() {
    try {
        const [custRes, venRes] = await Promise.all([
            fetch('/api/customers'),
            fetch('/api/vendors')
        ]);
        customersCache = await custRes.json();
        vendorsCache = await venRes.json();

        // Customers Table
        const custBody = document.getElementById('admin-customers-body');
        custBody.innerHTML = customersCache.map(c => `
            <tr>
                <td class="font-medium text-slate-800">#${c.id}</td>
                <td class="font-semibold text-slate-900">${c.customerName}</td>
                <td class="text-slate-600">${c.contact || '-'}</td>
                <td class="text-blue-700">${c.email || '-'}</td>
                <td class="text-slate-600">${c.address || '-'}</td>
                <td><button onclick="deleteCustomer(${c.id})" class="text-xs text-red-600 hover:text-red-800 font-medium">Delete</button></td>
            </tr>
        `).join('');

        // Vendors Table
        const venBody = document.getElementById('admin-vendors-body');
        venBody.innerHTML = vendorsCache.map(v => `
            <tr>
                <td class="font-medium text-slate-800">#${v.id}</td>
                <td class="font-semibold text-slate-900">${v.vendorName}</td>
                <td class="text-slate-600">${v.contact || '-'}</td>
                <td class="text-blue-700">${v.email || '-'}</td>
                <td class="text-slate-600">${v.address || '-'}</td>
                <td><button onclick="deleteVendor(${v.id})" class="text-xs text-red-600 hover:text-red-800 font-medium">Delete</button></td>
            </tr>
        `).join('');
    } catch (err) { console.error(err); }
}

async function submitCustomer(e) {
    e.preventDefault();
    const customerName = document.getElementById('cust-input-name').value;
    const contact = document.getElementById('cust-input-contact').value;
    const email = document.getElementById('cust-input-email').value;
    const address = document.getElementById('cust-input-addr').value;

    const res = await fetch('/api/customers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ customerName, contact, email, address })
    });
    if (res.ok) {
        showToast("Power Grid Customer saved in MySQL!");
        closeModal('modal-customer');
        e.target.reset();
        await loadAllData();
        loadParties();
    }
}

async function deleteCustomer(id) {
    if (!confirm("Are you sure?")) return;
    const res = await fetch(`/api/customers/${id}`, { method: 'DELETE' });
    if (res.ok) {
        showToast("Customer removed");
        await loadAllData();
        loadParties();
    }
}

async function submitVendor(e) {
    e.preventDefault();
    const vendorName = document.getElementById('ven-input-name').value;
    const contact = document.getElementById('ven-input-contact').value;
    const email = document.getElementById('ven-input-email').value;
    const address = document.getElementById('ven-input-addr').value;

    const res = await fetch('/api/vendors', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ vendorName, contact, email, address })
    });
    if (res.ok) {
        showToast("Tile Vendor saved in MySQL!");
        closeModal('modal-vendor');
        e.target.reset();
        await loadAllData();
        loadParties();
    }
}

async function deleteVendor(id) {
    if (!confirm("Are you sure?")) return;
    const res = await fetch(`/api/vendors/${id}`, { method: 'DELETE' });
    if (res.ok) {
        showToast("Vendor removed");
        await loadAllData();
        loadParties();
    }
}

// ==========================================
// USERS & ADMINS CONTROLLER
// ==========================================
async function loadUsersAndAdmins() {
    try {
        const [uRes, aRes] = await Promise.all([
            fetch('/api/auth/users'),
            fetch('/api/auth/admins')
        ]);
        const users = await uRes.json();
        const admins = await aRes.json();

        // Users
        const uBody = document.getElementById('admin-users-list-body');
        uBody.innerHTML = users.map(u => `
            <tr>
                <td class="font-medium text-slate-800">#${u.id}</td>
                <td class="font-semibold text-slate-900">${u.name}</td>
                <td class="text-blue-700">${u.email}</td>
                <td><span class="badge-tag badge-success">${u.role}</span></td>
                <td class="text-slate-600 text-xs">${u.createdAt || '-'}</td>
            </tr>
        `).join('');

        // Admins
        const aBody = document.getElementById('admin-admins-list-body');
        aBody.innerHTML = admins.map(a => `
            <tr>
                <td class="font-medium text-slate-800">#${a.id}</td>
                <td class="font-semibold text-slate-900">${a.name}</td>
                <td class="text-blue-700">${a.email}</td>
                <td class="text-slate-600 text-xs">${a.createdAt || '-'}</td>
            </tr>
        `).join('');
    } catch (err) { console.error(err); }
}

// ==========================================
// ACCOUNTING (CHART OF ACCOUNTS & LEDGER)
// ==========================================
async function loadAccounting() {
    try {
        const [acctRes, jnlRes] = await Promise.all([
            fetch('/api/accounts'),
            fetch('/api/journal-entries')
        ]);
        const accounts = await acctRes.json();
        const entries = await jnlRes.json();

        // 1. Chart of Accounts Table
        const acctBody = document.getElementById('admin-accounts-body');
        acctBody.innerHTML = accounts.map(a => {
            const typeClass = a.accountType === 'Asset' ? 'badge-success' :
                              a.accountType === 'Liability' ? 'badge-danger' :
                              a.accountType === 'Revenue' ? 'badge-info' : 'badge-warning';
            return `
                <tr>
                    <td class="font-medium text-slate-800">#${a.id}</td>
                    <td class="font-semibold text-slate-900">${a.accountName}</td>
                    <td><span class="badge-tag ${typeClass}">${a.accountType}</span></td>
                    <td class="font-bold text-slate-900">₹${Number(a.balanceInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                </tr>
            `;
        }).join('');

        // 2. Journal Entries Table
        const jnlBody = document.getElementById('admin-journal-body');
        jnlBody.innerHTML = entries.map(j => `
            <tr>
                <td class="font-semibold text-blue-700">JNL-${j.id}</td>
                <td class="text-slate-600 text-xs">${j.entryDate}</td>
                <td><span class="badge-tag badge-info">${j.journalType}</span></td>
                <td class="font-medium text-green-700">${j.debitAccount}</td>
                <td class="font-medium text-blue-700">${j.creditAccount}</td>
                <td class="font-bold text-slate-900">₹${Number(j.amountInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
            </tr>
        `).join('');

    } catch (err) { console.error(err); }
}

// ==========================================
// REPORTS CONTROLLERS
// ==========================================
async function loadPnLReport() {
    try {
        const res = await fetch('/api/reports/profit-loss');
        const pnl = await res.json();

        document.getElementById('rep-pnl-revenue').innerText = `₹${Number(pnl.energySalesRevenueInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-pnl-maint').innerText = `₹${Number(pnl.maintenanceExpensesInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-pnl-inverter').innerText = `₹${Number(pnl.inverterExpensesInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-pnl-total-exp').innerText = `₹${Number(pnl.totalExpensesInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-pnl-net').innerText = `₹${Number(pnl.netProfitInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-pnl-margin').innerText = `${pnl.profitMarginPercent}%`;
    } catch (err) { console.error(err); }
}

async function loadBudgetReport() {
    try {
        const res = await fetch('/api/reports/budget');
        const bgt = await res.json();

        document.getElementById('rep-bgt-plan-rev').innerText = `₹${Number(bgt.totalPlannedRevenueInr).toLocaleString('en-IN')}`;
        document.getElementById('rep-bgt-act-rev').innerText = `₹${Number(bgt.totalActualRevenueInr).toLocaleString('en-IN')}`;
        document.getElementById('rep-bgt-plan-exp').innerText = `₹${Number(bgt.totalPlannedExpensesInr).toLocaleString('en-IN')}`;
        document.getElementById('rep-bgt-act-exp').innerText = `₹${Number(bgt.totalActualExpensesInr).toLocaleString('en-IN')}`;

        const tbody = document.getElementById('rep-budget-table-body');
        tbody.innerHTML = (bgt.sectors || []).map(s => {
            const revVarClass = s.revenueVarianceInr >= 0 ? 'text-green-700 font-bold' : 'text-red-700 font-bold';
            const expVarClass = s.expenseVarianceInr >= 0 ? 'text-green-700 font-bold' : 'text-red-700 font-bold';
            return `
                <tr>
                    <td class="font-semibold text-slate-900">${s.sectorName}</td>
                    <td class="text-slate-600">${s.stationName}</td>
                    <td class="text-slate-600">${s.budgetYear}</td>
                    <td class="text-slate-700">₹${Number(s.plannedRevenueInr).toLocaleString('en-IN')}</td>
                    <td class="font-medium text-green-700">₹${Number(s.actualRevenueInr).toLocaleString('en-IN')}</td>
                    <td class="text-slate-700">₹${Number(s.plannedExpensesInr).toLocaleString('en-IN')}</td>
                    <td class="font-medium text-red-700">₹${Number(s.actualExpensesInr).toLocaleString('en-IN')}</td>
                    <td class="${revVarClass}">₹${Number(s.revenueVarianceInr).toLocaleString('en-IN')}</td>
                    <td class="${expVarClass}">₹${Number(s.expenseVarianceInr).toLocaleString('en-IN')}</td>
                </tr>
            `;
        }).join('');
    } catch (err) { console.error(err); }
}

async function loadEnergyReport() {
    try {
        const res = await fetch('/api/reports/energy');
        const eng = await res.json();

        document.getElementById('rep-eng-steps').innerText = Number(eng.totalSteps).toLocaleString();
        document.getElementById('rep-eng-gen').innerText = `${Number(eng.totalKwhGenerated).toFixed(2)} kWh`;
        document.getElementById('rep-eng-sold').innerText = `${Number(eng.totalKwhSold).toFixed(2)} kWh`;
        document.getElementById('rep-eng-reserve').innerText = `${Number(eng.availableReserveKwh).toFixed(2)} kWh`;
    } catch (err) { console.error(err); }
}

async function loadInvoiceReport() {
    try {
        const res = await fetch('/api/reports/invoices');
        const inv = await res.json();

        document.getElementById('rep-inv-total').innerText = `₹${Number(inv.totalInvoicedInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-inv-paid').innerText = `₹${Number(inv.totalPaidInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-inv-unpaid').innerText = `₹${Number(inv.totalUnpaidInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
    } catch (err) { console.error(err); }
}

async function loadVendorBillReport() {
    try {
        const res = await fetch('/api/reports/vendor-bills');
        const bills = await res.json();

        document.getElementById('rep-bill-total').innerText = `₹${Number(bills.totalBilledInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-bill-paid').innerText = `₹${Number(bills.totalPaidInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        document.getElementById('rep-bill-unpaid').innerText = `₹${Number(bills.totalUnpaidInr).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
    } catch (err) { console.error(err); }
}
