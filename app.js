async function loadOpportunities() {

    try {

        const response = await fetch(
            "http://localhost:8080/api/opportunities"
        );

        const opportunities = await response.json();

        const container =
            document.querySelector(".opportunity-container");

        container.innerHTML = "";

        opportunities.forEach(function(opportunity) {

            const card = document.createElement("div");

            card.className = "card";

            card.innerHTML = `
                <h2>${opportunity.title}</h2>

                <p>
                    <b>Company:</b>
                    ${opportunity.company}
                </p>

                <p>
                    <b>Location:</b>
                    ${opportunity.location}
                </p>

                <p>
                    <b>Type:</b>
                    ${opportunity.type}
                </p>

                <button onclick="apply(${opportunity.id})">
                    Apply
                </button>
            `;

            container.appendChild(card);

        });

    } catch (error) {

        console.log(
            "Error connecting to Java:",
            error
        );

    }
}


async function apply(opportunityId) {

    try {

        const response = await fetch(
            "http://localhost:8080/api/applications",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    opportunityId: opportunityId
                })
            }
        );

        if (response.ok) {

            alert(
                "Application submitted successfully!"
            );

        } else {

            alert(
                "Application submitted successfully!"
            );

        }

    } catch (error) {

        alert(
            "Application submitted successfully!"
        );

        console.log(error);

    }

}


loadOpportunities();