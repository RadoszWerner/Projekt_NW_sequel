import React from "react";
import { Paper, Typography, Box } from "@mui/material";

const ModLog = ({ log }) => {
  return (
    <Paper
      elevation={2}
      sx={{
        padding: "16px",
        marginTop: "10px",
        backgroundColor: "#f9f9f9",
      }}
    >
      <Typography variant="body1" sx={{ marginTop: "8px" }}>
        <strong>Moderator:</strong> {log.username} |{" "}
        {new Date(log.actionTime).toLocaleString("en-GB", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
        })}
      </Typography>
      <Typography variant="body1" sx={{ marginTop: "8px" }}>
        <strong>Action:</strong> {log.action}
      </Typography>
      {log.actionDetails && (
        <Typography variant="body2" sx={{ marginTop: "4px" }}>
          <strong>Details:</strong> {log.actionDetails}
        </Typography>
      )}
      {log.commentId && (
        <Box sx={{ marginTop: "8px" }}>
          <Typography variant="body2" color="textSecondary">
            Comment ID: {log.commentId}
          </Typography>
          <Typography variant="body1">
            <strong>Comment Content:</strong> {log.commentContent}
          </Typography>
        </Box>
      )}
      {log.postId && (
        <Box sx={{ marginTop: "8px" }}>
          <Typography variant="body2" color="textSecondary">
            Post ID: {log.postId}
          </Typography>
          <Typography variant="body1">
            <strong>Post Title:</strong> {log.postTitle}
          </Typography>
          <Typography variant="body1">
            <strong>Post Content:</strong> {log.postContent}
          </Typography>
        </Box>
      )}
    </Paper>
  );
};

export default ModLog;
