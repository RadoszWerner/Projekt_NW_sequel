import React from "react";
import { Paper, Typography, Button, Box, Grid2 } from "@mui/material";
import { getUserIdFromToken } from "../../auth";

const DeletedPost = ({ post, onRestoreSuccess }) => {
  const token = localStorage.getItem("token");
  console.log("Token from localStorage:", post);
  const handleRestore = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/deletedposts/restore",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            postId: post.id,
            userId: getUserIdFromToken(),
          }),
        }
      );

      const responseText = await response.text();

      if (response.ok) {
        alert(responseText);
        onRestoreSuccess(post.id);
      } else {
        alert(`Nie udało się przywrócić posta: ${responseText}`);
      }
    } catch (error) {
      console.error("Błąd przy przywracaniu posta:", error);
      alert("Wystąpił błąd podczas przywracania posta.");
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
            {post.username} |{" "}
            {new Date(post.deletedAt).toLocaleString("pl-PL", {
              year: "numeric",
              month: "2-digit",
              day: "2-digit",
              hour: "2-digit",
              minute: "2-digit",
            })}
          </Typography>

          <Typography variant="h6" sx={{ marginTop: "8px" }}>
            {post.titleContent}
          </Typography>

          <Typography variant="body1" sx={{ marginTop: "8px" }}>
            {post.commentContent}
          </Typography>

          <Typography
            variant="body2"
            color="textSecondary"
            sx={{ marginTop: "8px" }}
          >
            Reason: {post.reason}
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
          {/* Flagi toksyczności */}
          <Box
            sx={{
              display: "flex",
              flexWrap: "wrap",
              gap: "6px",
              marginBottom: "12px",
            }}
          >
            {[
              { label: "Toxic", value: post.toxic },
              { label: "Severe Toxic", value: post.severeToxic },
              { label: "Insult", value: post.insult },
              { label: "Threat", value: post.threat },
              { label: "Obscene", value: post.obscene },
              { label: "Identity Hate", value: post.identityHate },
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
            onClick={() => handleRestore(post.id)}
          >
            Restore
          </Button>
        </Grid2>
      </Grid2>
    </Paper>
  );
};

export default DeletedPost;
