import React from "react";
import { Button } from "reactstrap";

interface Props {
  id: string;
  text: string;
  color: string;
  children: string;
}

export const TweetButton: React.FC<Props> = (props) => {
  const internalUrl = `https://kenkoooo.com/atcoder/#/contest/show/${props.id}`;
  const shareUrl = `https://twitter.com/intent/tweet?url=${encodeURIComponent(
    internalUrl
  )}&text=${encodeURIComponent(props.text)}&hashtags=AtCoderProblems`;
  return (
    <Button
      href={shareUrl}
      rel="noopener noreferrer"
      target="_blank"
      color={props.color}
    >
      {props.children}
    </Button>
  );
};
