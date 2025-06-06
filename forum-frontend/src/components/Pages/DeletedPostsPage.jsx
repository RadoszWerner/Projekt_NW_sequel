import React, { useEffect, useState } from "react";
import { Box, Typography } from "@mui/material";
import { getRoleFromToken } from "../../auth";
import DeletedPost from "../Posts/DeletedPost";

const DeletedPostsPage = () => {
  const [deletedPosts, setDeletedPosts] = useState([]);
  const userRole = getRoleFromToken();

  useEffect(() => {
    if (userRole !== "moderator") {
      alert("Access denied. Only moderators can view this page.");
      return;
    }

    const fetchDeletedPosts = async () => {
      try {
        const response = await fetch("http://localhost:8080/deletedposts/all", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setDeletedPosts(data);
        } else {
          console.error("Nie udało się pobrać usuniętych postów");
        }
      } catch (error) {
        console.error("Błąd przy pobieraniu usuniętych postów:", error);
      }
    };

    fetchDeletedPosts();
  }, [userRole]);

  const handleRestoreSuccess = (restoredPostId) => {
    setDeletedPosts((prev) =>
      prev.filter((post) => post.id !== restoredPostId)
    );
  };

  if (userRole !== "moderator") {
    return null;
  }

  return (
    <Box sx={{ padding: "20px" }}>
      <Typography variant="h4" gutterBottom>
        Deleted Posts
      </Typography>

      {deletedPosts.length === 0 ? (
        <Typography variant="body1">No deleted posts found.</Typography>
      ) : (
        deletedPosts.map((post) => (
          <DeletedPost
            key={post.id}
            post={post}
            onRestoreSuccess={handleRestoreSuccess}
          />
        ))
      )}
    </Box>
  );
};

export default DeletedPostsPage;
