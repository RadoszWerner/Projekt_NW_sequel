import React from "react";
import { Paper, Typography, Button, Box, Grid2 } from "@mui/material";
import { getUserIdFromToken } from "../../auth";

const DeletedComment = ({ comment, onRestoreSuccess }) => {
  const handleRestore = async (commentId) => {
    try {
      const userId = getUserIdFromToken();
      console.log("User ID from token:", userId);
      const response = await fetch(
        "http://localhost:8080/deletedcomments/restore",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            commentId,
            userId,
          }),
        }
      );

      if (response.ok) {
        alert("Komentarz przywrócony!");
        onRestoreSuccess(commentId);
      } else {
        alert("Błąd przy przywracaniu komentarza");
      }
    } catch (error) {
      console.error("Błąd:", error);
      alert("Wystąpił problem z przywróceniem komentarza");
    }
  };

  const squareColor = (flag) => (flag ? "#4caf50" : "#e0e0e0");

  return (
    <Paper
      elevation={2}
      sx={{
        padding: "16px",
        marginTop: "10px",
        backgroundColor: "#f9f9f9",
      }}
    >
      <Grid2 container spacing={2} justifyContent="space-between">
        {/* Lewa kolumna */}
        <Grid2 item xs={10}>
          <Typography variant="body2" color="textSecondary">
            {comment.username} |{" "}
            {new Date(comment.deletedAt).toLocaleString("pl-PL", {
              year: "numeric",
              month: "2-digit",
              day: "2-digit",
              hour: "2-digit",
              minute: "2-digit",
            })}
          </Typography>

          <Typography variant="body1" sx={{ marginTop: "8px" }}>
            {comment.commentContent}
          </Typography>
          <Typography
            variant="body2"
            color="textSecondary"
            sx={{ marginTop: "8px" }}
          >
            Reason: {comment.reason}
          </Typography>
        </Grid2>

        {/* Prawa kolumna */}
        <Grid2
          item
          xs={2}
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "flex-end",
            justifyContent: "space-between",
          }}
        >
          {/* Kwadraty z flagami */}
          <Box
            sx={{
              display: "flex",
              flexWrap: "wrap",
              gap: "6px",
              marginBottom: "12px",
            }}
          >
            {[
              { label: "Toxic", value: comment.toxic },
              { label: "Severe Toxic", value: comment.severeToxic },
              { label: "Insult", value: comment.insult },
              { label: "Threat", value: comment.threat },
              { label: "Obscene", value: comment.obscene },
              { label: "Identity Hate", value: comment.identityHate },
            ].map((flag, index) => (
              <Box
                key={index}
                title={flag.label}
                sx={{
                  width: 16,
                  height: 16,
                  backgroundColor: squareColor(flag.value),
                  borderRadius: "2px",
                  border: "1px solid #ccc",
                }}
              />
            ))}
          </Box>

          {/* Przycisk Restore */}
          <Button
            variant="contained"
            color="primary"
            size="small"
            onClick={() => handleRestore(comment.id)}
          >
            Restore
          </Button>
        </Grid2>
      </Grid2>
    </Paper>
  );
};

export default DeletedComment;
