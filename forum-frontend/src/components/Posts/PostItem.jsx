import React from "react";
import { Paper, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
  post: {
    marginBottom: "15px",
    padding: "10px",
  },
});

const PostItem = ({ post, onClick }) => {
  const classes = useStyles();

  return (
    <Paper
      className={classes.post}
      elevation={3}
      onClick={() => onClick(post.id)}
    >
      <Typography variant="h6">{post.title}</Typography>
      <Typography variant="body2" color="textSecondary">
        By: {post.user.username} | {new Date(post.createdAt).toLocaleString()}
      </Typography>
      <Typography variant="body1">{post.content}</Typography>
    </Paper>
  );
};

export default PostItem;
