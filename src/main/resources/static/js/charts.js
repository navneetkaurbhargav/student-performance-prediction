/**
 * Chart.js configuration for analytics
 */

class PerformanceCharts {
    constructor() {
        this.colors = {
            primary: '#007bff',
            success: '#28a745',
            danger: '#dc3545',
            warning: '#ffc107',
            info: '#17a2b8',
            light: '#f8f9fa',
            dark: '#343a40'
        };
    }
    
    initAllCharts() {
        this.initPassFailChart();
        this.initAttendanceChart();
        this.initPerformanceTrends();
        this.initRiskDistribution();
    }
    
    initPassFailChart() {
        const ctx = document.getElementById('passFailChart');
        if (!ctx) return;
        
        new Chart(ctx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['Pass', 'Fail', 'Borderline'],
                datasets: [{
                    data: [65, 25, 10],
                    backgroundColor: [
                        this.colors.success,
                        this.colors.danger,
                        this.colors.warning
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Overall Pass/Fail Distribution'
                    }
                }
            }
        });
    }
    
    initAttendanceChart() {
        const ctx = document.getElementById('attendanceChart');
        if (!ctx) return;
        
        new Chart(ctx.getContext('2d'), {
            type: 'bar',
            data: {
                labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4', 'Week 5', 'Week 6'],
                datasets: [{
                    label: 'Average Attendance (%)',
                    data: [85, 82, 88, 90, 87, 85],
                    backgroundColor: this.colors.primary,
                    borderColor: this.colors.dark,
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        title: {
                            display: true,
                            text: 'Attendance Rate (%)'
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Weekly Attendance Trends'
                    }
                }
            }
        });
    }
    
    initPerformanceTrends() {
        const ctx = document.getElementById('performanceTrends');
        if (!ctx) return;
        
        new Chart(ctx.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Average GPA',
                    data: [2.8, 2.9, 3.0, 3.1, 3.2, 3.1],
                    borderColor: this.colors.success,
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.3
                }, {
                    label: 'Pass Rate',
                    data: [70, 72, 75, 78, 80, 82],
                    borderColor: this.colors.primary,
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Value'
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Monthly Performance Trends'
                    }
                }
            }
        });
    }
    
    initRiskDistribution() {
        const ctx = document.getElementById('riskDistribution');
        if (!ctx) return;
        
        new Chart(ctx.getContext('2d'), {
            type: 'polarArea',
            data: {
                labels: ['High Risk', 'Medium Risk', 'Low Risk', 'Excellent'],
                datasets: [{
                    data: [15, 25, 40, 20],
                    backgroundColor: [
                        this.colors.danger,
                        this.colors.warning,
                        this.colors.info,
                        this.colors.success
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Student Risk Level Distribution'
                    }
                }
            }
        });
    }
    
    // Real-time update for dashboard
    updateChartData(chartId, newData) {
        const chart = Chart.getChart(chartId);
        if (chart) {
            chart.data.datasets.forEach((dataset, i) => {
                dataset.data = newData[i];
            });
            chart.update();
        }
    }
}

// Initialize charts when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    const charts = new PerformanceCharts();
    charts.initAllCharts();
    
    // Auto-refresh data every 30 seconds for admin dashboard
    if (window.location.pathname.includes('/admin/dashboard')) {
        setInterval(() => {
            fetch('/api/system/stats')
                .then(response => response.json())
                .then(data => {
                    // Update dashboard stats
                    document.querySelectorAll('.stat-card').forEach(card => {
                        const statType = card.dataset.stat;
                        if (data[statType]) {
                            card.querySelector('h3').textContent = data[statType];
                        }
                    });
                })
                .catch(console.error);
        }, 30000);
    }
});