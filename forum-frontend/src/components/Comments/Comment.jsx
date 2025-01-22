import React, { useState } from "react";
import {
  Paper,
  Typography,
  Stack,
  Button,
  TextField,
  Box,
} from "@mui/material";
import {
  getUserIdFromToken,
  getRoleFromToken,
  getUsernameFromToken,
} from "../../auth";

const Comment = ({ comment }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(comment.content);

  const userId = getUserIdFromToken();
  const userRole = getRoleFromToken();
  const username = getUsernameFromToken();
  const token = localStorage.getItem("token");

  const canEditOrDelete =
    userId && (userId === comment.user.id || userRole === "moderator");

  const handleEdit = async () => {
    if (!editedContent.trim()) {
      alert("Comment content cannot be empty.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/comments/edit", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          commentId: comment.id,
          username,
          newContent: editedContent,
        }),
      });

      const responseText = await response.text();

      if (response.ok) {
        comment.content = editedContent;
        setEditedContent("");
        setIsEditing(false);
        alert(responseText);
      } else {
        alert(`Failed to update comment: ${responseText}`);
      }
    } catch (error) {
      console.error("Error editing comment:", error);
      alert("An error occurred while editing the comment.");
    }
  };

  const handleDelete = async () => {
    try {
      const response = await fetch("http://localhost:8080/comments/delete", {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ commentId: comment.id, username }), // Przekazujesz commentId i username
      });

      const responseText = await response.text();

      if (response.ok) {
        comment.deleted = true;
        alert(responseText);
      } else {
        alert(`Failed to delete comment: ${responseText}`);
      }
    } catch (error) {
      console.error("Error deleting comment:", error);
      alert("An error occurred while deleting the comment.");
    }
  };

  if (comment.deleted) {
    return null; // Nie renderuj usuniętych komentarzy
  }

  return (
    <Paper
      elevation={2}
      sx={{
        padding: "10px",
        marginTop: "10px",
        backgroundColor: "#f9f9f9",
      }}
    >
      {isEditing ? (
        <Box>
          <TextField
            fullWidth
            multiline
            rows={3}
            value={editedContent}
            onChange={(e) => setEditedContent(e.target.value)}
          />
          <Stack direction="row" spacing={1} sx={{ marginTop: "10px" }}>
            <Button variant="contained" color="primary" onClick={handleEdit}>
              Save
            </Button>
            <Button
              variant="contained"
              color="secondary"
              onClick={() => setIsEditing(false)}
            >
              Cancel
            </Button>
          </Stack>
        </Box>
      ) : (
        <>
          <Typography variant="body2" color="textSecondary">
            {comment.user.username} |{" "}
            {new Date(comment.createdAt).toLocaleString()}
          </Typography>
          <Typography variant="body1">{comment.content}</Typography>
          {canEditOrDelete && (
            <Stack direction="row" spacing={1} sx={{ marginTop: "10px" }}>
              <Button
                variant="outlined"
                size="small"
                onClick={() => setIsEditing(true)}
              >
                Edit
              </Button>
              <Button
                variant="outlined"
                color="secondary"
                size="small"
                onClick={handleDelete}
              >
                Delete
              </Button>
            </Stack>
          )}
        </>
      )}
    </Paper>
  );
};

export default Comment;
