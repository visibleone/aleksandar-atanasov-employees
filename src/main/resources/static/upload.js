document.getElementById("uploadButton").addEventListener("click", () => {
    const fileInput = document.getElementById("fileInput");
    const formData = new FormData();

    // Ensure a file is selected
    if (fileInput.files.length === 0) {
        displayMessage("No file selected. Please choose a file to upload.", "error");
        return;
    }

    // Append the file to FormData
    formData.append("file", fileInput.files[0]);

    // Make the API POST request
    fetch("http://localhost:8080/api/v1/employees/common-projects", {
        method: "POST",
        body: formData,
    })
        .then((response) => {
            console.log("Response:", response);
            if (response.status === 202) {
                displayMessage("File accepted for processing. Polling for results...", "success");

                // Get the process ID from the response headers
                const processId = response.headers.get("X-Identification-Process-ID");
                console.log("Process ID:", processId);

                // Start polling the result endpoint
                pollResultEndpoint(processId);
            } else if (response.status === 400) {
                displayMessage("Validation failed. Ensure the file is valid.", "error");
                console.error("Validation error:", response.statusText);
            } else if (response.status === 413) {
                displayMessage("File too large. Reduce the file size and try again.", "error");
            } else {
                displayMessage("An error occurred. Please try again.", "error");
                console.error("Error:", response.statusText);
            }
        })
        .catch((error) => {
            displayMessage("An unexpected error occurred. Check the console for details.", "error");
            console.error("Error:", error);
        });
});

// Function to poll the result endpoint
function pollResultEndpoint(processId) {
    const pollInterval = 2000; // Poll every 2 seconds
    const pollUrl = `http://localhost:8080/api/v1/employees/common-projects/result/${processId}`;

    const poll = setInterval(() => {
        fetch(pollUrl, { method: "GET" })
            .then((response) => response.json())
            .then((data) => {
                console.log("Polling result:", data);

                if (data.status === "SUCCESS") {
                    clearInterval(poll);
                    displayMessage("Processing completed successfully. Displaying results...", "success");
                    displayCommonProjects(data.commonProjects);
                } else if (data.status === "FAILED") {
                    clearInterval(poll);
                    displayMessage(`Processing failed: ${data.errorMessage}`, "error");
                }
            })
            .catch((error) => {
                console.error("Error while polling:", error);
                displayMessage("An error occurred while polling. Check the console for details.", "error");
            });
    }, pollInterval);
}

// Function to display common projects in a table
function displayCommonProjects(commonProjects) {
    const responseMessage = document.getElementById("responseMessage");

    if (commonProjects.length === 0) {
        responseMessage.textContent = "No common projects found.";
        responseMessage.style.color = "green";
        return;
    }

    // Create a table to display the common projects
    const table = document.createElement("table");
    table.setAttribute("border", "1");
    table.style.marginTop = "20px";

    // Table header
    const headerRow = document.createElement("tr");
    headerRow.innerHTML = `
        <th>First Employee ID</th>
        <th>Second Employee ID</th>
        <th>Project ID</th>
        <th>Days Worked</th>
    `;
    table.appendChild(headerRow);

    // Table rows for each common project
    commonProjects.forEach((project) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${project.firstEmployeeId}</td>
            <td>${project.secondEmployeeId}</td>
            <td>${project.projectId}</td>
            <td>${project.daysWorked}</td>
        `;
        table.appendChild(row);
    });

    // Append the table to the web page
    responseMessage.textContent = ""; // Clear any existing message
    responseMessage.style.color = "black"; // Reset message color
    responseMessage.appendChild(table);
}

// Function to display messages to the user
function displayMessage(message, type) {
    const responseMessage = document.getElementById("responseMessage");
    responseMessage.textContent = message;
    responseMessage.style.color = type === "error" ? "red" : "green";
}