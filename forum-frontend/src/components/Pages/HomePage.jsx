import React, { useEffect, useState } from "react";
import { Container, CircularProgress } from "@mui/material";
import { makeStyles } from "@mui/styles";
import PostList from "../Posts/PostList";

const useStyles = makeStyles({
  content: {
    flexGrow: 1,
    padding: "20px",
    overflowY: "auto",
  },
});

const HomePage = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const classes = useStyles();

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/posts/all", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setPosts(data);
        console.log(data);

        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching posts:", error);
        setLoading(false);
      });
  }, []);

  return (
    <Container className={classes.content}>
      {loading ? <CircularProgress /> : <PostList posts={posts} />}
    </Container>
  );
};

export default HomePage;
