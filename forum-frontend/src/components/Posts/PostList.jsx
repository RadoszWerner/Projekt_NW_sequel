import React, { useState } from "react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Pagination,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import PostItem from "./PostItem";
import { getUsernameFromToken } from "../../auth";

const PostList = ({ posts }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [openDialog, setOpenDialog] = useState(false);
  const [newPostTitle, setNewPostTitle] = useState("");
  const [newPostContent, setNewPostContent] = useState("");
  const postsPerPage = 5;
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const handleChangePage = (event, value) => {
    setCurrentPage(value);
  };

  const handlePostClick = (post) => {
    if (!post.id) {
      console.error("Post ID is undefined:", post);
      return;
    }
    navigate(`/post/${post.id}`, { state: { post } }); // Używamy `post.id` w ścieżce
  };

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleCreatePost = async () => {
    if (!newPostTitle.trim() || !newPostContent.trim()) {
      alert("Both title and content are required.");
      return;
    }

    const username = getUsernameFromToken(); // Pobierz nazwę użytkownika z tokena

    try {
      const response = await fetch("http://localhost:8080/posts/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          username: username,
          title: newPostTitle,
          content: newPostContent,
        }),
      });

      const responseText = await response.text();

      if (response.ok) {
        alert(responseText); // Pokazuje komunikat zwrócony przez backend
        handleCloseDialog(); // Zamknięcie okna dialogowego
        // Opcjonalnie: możesz zaktualizować stan listy postów, jeśli chcesz
      } else {
        alert(`Failed to create post: ${responseText}`);
      }
    } catch (error) {
      console.error("Error creating post:", error);
      alert("An error occurred while creating the post.");
    }
  };

  const paginatedPosts = posts.slice(
    (currentPage - 1) * postsPerPage,
    currentPage * postsPerPage
  );

  return (
    <Box>
      {/* Button to open the "Add Post" dialog */}
      <Button
        variant="contained"
        color="primary"
        onClick={handleOpenDialog}
        sx={{ marginBottom: "20px" }}
      >
        Add Post
      </Button>

      {/* List of Posts */}
      {paginatedPosts.map((post) => (
        <PostItem
          key={post.id}
          post={post}
          onClick={() => handlePostClick(post)}
        />
      ))}

      {/* Pagination */}
      <Pagination
        count={Math.ceil(posts.length / postsPerPage)}
        page={currentPage}
        onChange={handleChangePage}
        sx={{ marginTop: "20px", display: "flex", justifyContent: "center" }}
      />

      {/* Dialog for creating a new post */}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Create a New Post</DialogTitle>
        <DialogContent>
          <TextField
            label="Title"
            fullWidth
            margin="normal"
            value={newPostTitle}
            onChange={(e) => setNewPostTitle(e.target.value)}
          />
          <TextField
            label="Content"
            fullWidth
            margin="normal"
            multiline
            rows={4}
            value={newPostContent}
            onChange={(e) => setNewPostContent(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="secondary">
            Cancel
          </Button>
          <Button onClick={handleCreatePost} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PostList;
