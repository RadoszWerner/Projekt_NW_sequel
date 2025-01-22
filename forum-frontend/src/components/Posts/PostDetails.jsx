import React, { useEffect, useState } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useNavigate, useLocation, useParams } from "react-router-dom";
import {
  getUserIdFromToken,
  getRoleFromToken,
  getUsernameFromToken,
} from "../../auth";
import Comment from "../Comments/Comment";

const PostDetails = () => {
  const { id } = useParams();
  const location = useLocation();
  const [post, setPost] = useState(location.state?.post || null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState("");
  const [isEditDialogOpen, setEditDialogOpen] = useState(false);
  const [editTitle, setEditTitle] = useState("");
  const [editContent, setEditContent] = useState("");
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = getUserIdFromToken();
  const userRole = getRoleFromToken();
  const username = getUsernameFromToken();
  useEffect(() => {
    const fetchPost = async () => {
      if (post) return;
      try {
        const response = await fetch(`http://localhost:8080/posts/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await response.json();
        setPost(data);
      } catch (error) {
        console.error("Error fetching post:", error);
      }
    };

    const fetchComments = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/comments/post/${id}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        const data = await response.json();
        console.log(data);
        setComments(data);
      } catch (error) {
        console.error("Error fetching comments:", error);
      }
    };

    const fetchData = async () => {
      setLoading(true);
      await Promise.all([fetchPost(), fetchComments()]);
      setLoading(false);
    };

    fetchData();
  }, [id, post, token]);

  const handleEditOpen = () => {
    setEditTitle(post.title);
    setEditContent(post.content);
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
  };

  const handleEditSave = async () => {
    if (!username) {
      alert("Nie można pobrać nazwy użytkownika.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/posts/edit", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          postId: post.id,
          username,
          newTitle: editTitle,
          newContent: editContent,
        }),
      });

      const responseText = await response.text();

      if (response.ok) {
        setPost((prevPost) => ({
          ...prevPost,
          title: editTitle,
          content: editContent,
        }));

        setEditDialogOpen(false);
        alert("Post updated successfully.");
      } else {
        alert(`Failed to update post: ${responseText}`);
      }
    } catch (error) {
      console.error("Error updating post:", error);
      alert("An error occurred while updating the post.");
    }
  };

  const handleDelete = async () => {
    if (!username) {
      alert("Nie można pobrać nazwy użytkownika.");
      return;
    }
    try {
      const response = await fetch("http://localhost:8080/posts/delete", {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ postId: id, username }),
      });

      if (response.ok) {
        alert("Post deleted successfully.");
        navigate("/home");
      } else {
        const errorMsg = await response.text();
        alert(`Failed to delete post: ${errorMsg}`);
      }
    } catch (error) {
      console.error("Error deleting post:", error);
      alert("An error occurred while deleting the post.");
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) {
      alert("Comment cannot be empty.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/comments/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          postId: id,
          username,
          content: newComment,
        }),
      });

      const responseText = await response.text();

      if (response.ok) {
        const newCommentData = {
          id: Date.now(),
          user: { username },
          content: newComment,
          createdAt: new Date().toISOString(),
        };

        setComments((prevComments) => [...prevComments, newCommentData]);
        setNewComment("");
        alert(responseText);
      } else {
        alert(`Failed to add comment: ${responseText}`);
      }
    } catch (error) {
      console.error("Error adding comment:", error);
      alert("An error occurred while adding the comment.");
    }
  };

  if (loading) {
    return <CircularProgress />;
  }

  const canEditOrDelete =
    userId && (userId === post.user.id || userRole === "moderator");

  return (
    <Box>
      <Button
        variant="outlined"
        color="primary"
        sx={{ marginBottom: "20px" }}
        onClick={() => navigate(-1)}
      >
        Back to Previous Page
      </Button>
      <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px" }}>
        <Typography variant="h4">{post.title}</Typography>
        <Typography variant="body2" color="textSecondary">
          By: {post.user.username} | {new Date(post.createdAt).toLocaleString()}
        </Typography>
        <Typography variant="body1" sx={{ marginTop: "10px" }}>
          {post.content}
        </Typography>
        {canEditOrDelete && (
          <Stack direction="row" spacing={2} sx={{ marginTop: "20px" }}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleEditOpen}
            >
              Edit
            </Button>
            <Button
              variant="contained"
              color="secondary"
              onClick={handleDelete}
            >
              Delete
            </Button>
          </Stack>
        )}
      </Paper>

      <Typography variant="h6">Comments:</Typography>
      {comments
        .filter((comment) => !comment.deleted)
        .map((comment) => (
          <Comment key={comment.id} comment={comment} />
        ))}
      <Box sx={{ marginTop: "20px" }}>
        <TextField
          label="Add a comment"
          fullWidth
          multiline
          rows={3}
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
        />
        <Button
          variant="contained"
          color="primary"
          sx={{ marginTop: "10px" }}
          onClick={handleAddComment}
        >
          Add Comment
        </Button>
      </Box>
      {/* Edit Post Dialog */}
      <Dialog open={isEditDialogOpen} onClose={handleEditClose}>
        <DialogTitle>Edit Post</DialogTitle>
        <DialogContent>
          <TextField
            label="Title"
            fullWidth
            margin="normal"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
          />
          <TextField
            label="Content"
            fullWidth
            margin="normal"
            multiline
            rows={4}
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose} color="secondary">
            Cancel
          </Button>
          <Button onClick={handleEditSave} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PostDetails;
