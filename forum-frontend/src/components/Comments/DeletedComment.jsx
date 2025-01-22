import React from "react";
import { Paper, Typography, Button, Stack } from "@mui/material";

const DeletedComment = ({ comment, onRestoreSuccess }) => {
  const token = localStorage.getItem("token");

  const handleRestore = async () => {
    try {
      const response = await fetch("http://localhost:8080/comments/restore", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ commentId: comment.id }),
      });

      const responseText = await response.text();

      if (response.ok) {
        alert(responseText);
        onRestoreSuccess(comment.id);
      } else {
        alert(`Failed to restore comment: ${responseText}`);
      }
    } catch (error) {
      console.error("Error restoring comment:", error);
      alert("An error occurred while restoring the comment.");
    }
  };
  console.log(comment);
  return (
    <Paper
      elevation={2}
      sx={{
        padding: "10px",
        marginTop: "10px",
        backgroundColor: "#f9f9f9",
      }}
    >
      <Typography variant="body2" color="textSecondary">
        {comment.user.username} | {new Date(comment.createdAt).toLocaleString()}
      </Typography>
      <Typography variant="body1" sx={{ marginBottom: "10px" }}>
        {comment.content}
      </Typography>
      <Stack direction="row" spacing={1}>
        <Button variant="contained" color="primary" onClick={handleRestore}>
          Restore
        </Button>
      </Stack>
    </Paper>
  );
};

export default DeletedComment;
