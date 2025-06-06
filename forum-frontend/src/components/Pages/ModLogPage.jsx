import React, { useEffect, useState } from "react";
import { Container, CircularProgress, Typography, Alert } from "@mui/material";
import { makeStyles } from "@mui/styles";
import ModLog from "../Logs/ModLog";
import { getRoleFromToken } from "../../auth";

const useStyles = makeStyles({
  content: {
    flexGrow: 1,
    padding: "20px",
    overflowY: "auto",
  },
});

const ModLogsPage = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const classes = useStyles();
  const userRole = getRoleFromToken();

  useEffect(() => {
    if (userRole !== "moderator") {
      setError("Brak dostępu. Tylko moderatorzy mogą wyświetlać logi.");
      setLoading(false);
      return;
    }

    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/mod-logs/all", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Nie udało się pobrać logów");
        }
        return response.json();
      })
      .then((data) => {
        setLogs(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Błąd podczas pobierania logów:", error);
        setError("Wystąpił błąd podczas pobierania logów.");
        setLoading(false);
      });
  }, [userRole]);

  if (error) {
    return (
      <Container className={classes.content}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container className={classes.content}>
      <Typography variant="h4" gutterBottom>
        Moderator Logs:
      </Typography>
      {loading ? (
        <CircularProgress />
      ) : logs.length === 0 ? (
        <Typography variant="body1">No logs to display.</Typography>
      ) : (
        logs.map((log) => <ModLog key={log.id} log={log} />)
      )}
    </Container>
  );
};

export default ModLogsPage;
