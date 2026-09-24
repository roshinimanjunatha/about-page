const express = require("express");

const app = express();
app.use(express.json());
let opportunities = [];
app.post("/api/opportunities", (req, res) => {
app.get("/api/opportunities", (req, res) => {
    res.json(opportunities);
});
let applications = [];
app.post("/api/applications", (req, res) => {
app.get("/api/applications", (req, res) => {
    res.json(applications);
});
app.put("/api/applications/:id", (req, res) => {

    const application = applications.find(
        app => app.id === parseInt(req.params.id)
    );

    if (!application) {
        return res.status(404).json({
            message: "Application not found"
        });
    }

    application.status = req.body.status;

    res.json({
        message: "Application status updated",
        application: application
    });
});
    const application = {
        id: applications.length + 1,
        studentId: req.body.studentId,
        opportunityId: req.body.opportunityId,
        status: "Applied"
    };

    applications.push(application);

    res.json({
        message: "Application submitted successfully",
        application: application
    });
});
    const opportunity = {
        id: opportunities.length + 1,
        title: req.body.title,
        company: req.body.company,
        type: req.body.type,
        location: req.body.location
    };

    opportunities.push(opportunity);

    res.json({
        message: "Opportunity created successfully",
        opportunity: opportunity
    });
});
app.get("/", (req, res) => {
    res.send("CampusConnect Backend is Working!");
});

app.listen(3000, () => {
    console.log("Server running on http://localhost:3000");
});