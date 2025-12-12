/**
 * Main JavaScript file for Student Performance System
 */

// Form validation and enhancement
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Add prediction form validation
    const predictionForm = document.querySelector('form[th\\:object*="predictionRequest"]');
    if (predictionForm) {
        predictionForm.addEventListener('submit', function(e) {
            const inputs = this.querySelectorAll('input[type="number"]');
            let isValid = true;
            
            inputs.forEach(input => {
                const min = parseFloat(input.min);
                const max = parseFloat(input.max);
                const value = parseFloat(input.value);
                
                if (isNaN(value) || value < min || value > max) {
                    isValid = false;
                    input.classList.add('is-invalid');
                    
                    // Create error message
                    let errorDiv = input.nextElementSibling;
                    if (!errorDiv || !errorDiv.classList.contains('invalid-feedback')) {
                        errorDiv = document.createElement('div');
                        errorDiv.className = 'invalid-feedback';
                        input.parentNode.appendChild(errorDiv);
                    }
                    errorDiv.textContent = `Please enter a value between ${min} and ${max}`;
                } else {
                    input.classList.remove('is-invalid');
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                showAlert('Please correct the errors in the form.', 'danger');
            }
        });
    }
    
    // Auto-calculate GPA from percentage if needed
    const gpaInput = document.querySelector('input[th\\:field*="previousGPA"]');
    if (gpaInput) {
        gpaInput.addEventListener('blur', function() {
            let value = parseFloat(this.value);
            if (!isNaN(value) && value > 4) {
                // Convert percentage to GPA (assuming 100% = 4.0)
                const gpaValue = (value / 100) * 4;
                this.value = gpaValue.toFixed(2);
            }
        });
    }
    
    // Add more student rows in batch prediction
    const addStudentBtn = document.getElementById('addStudentBtn');
    if (addStudentBtn) {
        addStudentBtn.addEventListener('click', function() {
            const tableBody = document.querySelector('#studentsTable tbody');
            const rowCount = tableBody.querySelectorAll('tr').length;
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td>${rowCount + 1}</td>
                <td><input type="number" class="form-control form-control-sm" name="attendanceRate" min="0" max="100" step="0.1" required></td>
                <td><input type="number" class="form-control form-control-sm" name="previousGPA" min="0" max="4" step="0.01" required></td>
                <td><input type="number" class="form-control form-control-sm" name="studyHoursWeekly" min="0" max="40" required></td>
                <td><input type="number" class="form-control form-control-sm" name="assignmentScoresAvg" min="0" max="100" step="0.1" required></td>
                <td><input type="number" class="form-control form-control-sm" name="extracurricularHours" min="0" max="20" required></td>
                <td><input type="number" class="form-control form-control-sm" name="familySupport" min="1" max="5" required></td>
                <td><input type="number" class="form-control form-control-sm" name="financialStability" min="1" max="5" required></td>
                <td><button type="button" class="btn btn-sm btn-danger remove-row">Remove</button></td>
            `;
            tableBody.appendChild(newRow);
            
            // Add remove functionality
            newRow.querySelector('.remove-row').addEventListener('click', function() {
                this.closest('tr').remove();
                updateRowNumbers();
            });
            
            updateRowNumbers();
        });
    }
    
    // Initialize remove buttons
    document.querySelectorAll('.remove-row').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('tr').remove();
            updateRowNumbers();
        });
    });
    
    function updateRowNumbers() {
        document.querySelectorAll('#studentsTable tbody tr').forEach((row, index) => {
            row.querySelector('td:first-child').textContent = index + 1;
        });
    }
    
    // Model activation confirmation
    document.querySelectorAll('a[href*="/activate/"]').forEach(link => {
        link.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to activate this model? This will deactivate the current active model.')) {
                e.preventDefault();
            }
        });
    });
    
    // Show loading indicator for retraining
    const retrainForm = document.querySelector('form[th\\:object*="retrainingRequest"]');
    if (retrainForm) {
        retrainForm.addEventListener('submit', function() {
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="loading"></span> Processing...';
            
            // Show processing message
            showAlert('Retraining request submitted. Processing in background via JMS queue...', 'info');
        });
    }
});

// Utility functions
function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.container') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// Export predictions to CSV
function exportToCSV() {
    const table = document.querySelector('table');
    if (!table) return;
    
    let csv = [];
    const rows = table.querySelectorAll('tr');
    
    rows.forEach(row => {
        const rowData = [];
        const cols = row.querySelectorAll('td, th');
        
        cols.forEach(col => {
            // Clean up the data (remove buttons, etc.)
            let text = col.innerText;
            text = text.replace(/\n/g, ' ').replace(/\s+/g, ' ').trim();
            rowData.push(`"${text}"`);
        });
        
        csv.push(rowData.join(','));
    });
    
    const csvContent = csv.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', 'predictions.csv');
    link.style.visibility = 'hidden';
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// AJAX prediction for single student
function quickPredict() {
    const formData = {
        attendanceRate: document.getElementById('quickAttendance').value,
        previousGPA: document.getElementById('quickGPA').value,
        studyHoursWeekly: document.getElementById('quickStudyHours').value,
        assignmentScoresAvg: document.getElementById('quickAssignments').value,
        extracurricularHours: document.getElementById('quickExtracurricular').value,
        familySupport: document.getElementById('quickFamily').value,
        financialStability: document.getElementById('quickFinancial').value
    };
    
    // Validate
    for (const [key, value] of Object.entries(formData)) {
        if (!value || value.trim() === '') {
            showAlert(`Please fill in ${key}`, 'warning');
            return;
        }
    }
    
    // Show loading
    const predictBtn = document.getElementById('quickPredictBtn');
    const originalText = predictBtn.innerHTML;
    predictBtn.disabled = true;
    predictBtn.innerHTML = '<span class="loading"></span> Predicting...';
    
    // Make API call
    fetch('/api/predict', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    })
    .then(response => response.json())
    .then(data => {
        // Display results
        const resultDiv = document.getElementById('quickResult');
        resultDiv.innerHTML = `
            <div class="alert ${data.prediction === 'PASS' ? 'alert-success' : 'alert-danger'}">
                <h5>Prediction: ${data.prediction}</h5>
                <p>Passing Probability: ${data.passingProbability.toFixed(2)}%</p>
                <p>Confidence: ${(data.confidenceScore * 100).toFixed(2)}%</p>
                <p><small>Model: ${data.modelUsed}</small></p>
            </div>
        `;
        
        // Reset button
        predictBtn.disabled = false;
        predictBtn.innerHTML = originalText;
        
        // Scroll to result
        resultDiv.scrollIntoView({ behavior: 'smooth' });
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('Prediction failed. Please try again.', 'danger');
        predictBtn.disabled = false;
        predictBtn.innerHTML = originalText;
    });
}

// Initialize quick predict form if it exists
document.addEventListener('DOMContentLoaded', function() {
    const quickPredictBtn = document.getElementById('quickPredictBtn');
    if (quickPredictBtn) {
        quickPredictBtn.addEventListener('click', quickPredict);
    }
    
    // Add export button to tables
    const tables = document.querySelectorAll('table');
    tables.forEach(table => {
        if (!table.parentNode.querySelector('.export-btn')) {
            const exportBtn = document.createElement('button');
            exportBtn.className = 'btn btn-sm btn-outline-secondary export-btn mb-2';
            exportBtn.innerHTML = '📥 Export CSV';
            exportBtn.onclick = exportToCSV;
            table.parentNode.insertBefore(exportBtn, table);
        }
    });
});