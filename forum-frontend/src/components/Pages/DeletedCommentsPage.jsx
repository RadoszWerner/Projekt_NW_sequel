import React, { useEffect, useState } from "react";
import { Box, Typography } from "@mui/material";
import { getRoleFromToken } from "../../auth";
import DeletedComment from "../Comments/DeletedComment";

const DeletedCommentsPage = () => {
  const [deletedComments, setDeletedComments] = useState([]);
  const userRole = getRoleFromToken();

  useEffect(() => {
    if (userRole !== "moderator") {
      console.log(userRole);
      alert("Access denied. Only moderators can view this page.");
      return;
    }

    const fetchDeletedComments = async () => {
      try {
        const response = await fetch("http://localhost:8080/comments/deleted", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setDeletedComments(data);
        } else {
          console.error("Failed to fetch deleted comments");
        }
      } catch (error) {
        console.error("Error fetching deleted comments:", error);
      }
    };

    fetchDeletedComments();
  }, [userRole]);

  const handleRestoreSuccess = (restoredCommentId) => {
    setDeletedComments((prev) =>
      prev.filter((comment) => comment.id !== restoredCommentId)
    );
  };

  if (userRole !== "moderator") {
    return null;
  }

  return (
    <Box sx={{ padding: "20px" }}>
      <Typography variant="h4" gutterBottom>
        Deleted Comments
      </Typography>
      {deletedComments.length === 0 ? (
        <Typography variant="body1">No deleted comments found.</Typography>
      ) : (
        deletedComments.map((comment) => (
          <DeletedComment
            key={comment.id}
            comment={comment}
            onRestoreSuccess={handleRestoreSuccess}
          />
        ))
      )}
    </Box>
  );
};

export default DeletedCommentsPage;
