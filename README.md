To make project work add following SQL statements :

CREATE SCHEMA projekt_zespolowy;

INSERT INTO roles (name) VALUES ('ROLE_USER');

INSERT INTO roles (name) VALUES ('ROLE_MODERATOR');

INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO games (name) VALUES ('GAME_TICTACTOE');

INSERT INTO games (name) VALUES ('GAME_SHIPS');

INSERT INTO games (name) VALUES ('GAME_ROCK_PAPER_SCISSORS');

To play with another player on different devices, you need to change the similiar looking line in class ChangeCredentialsService, LoginService, RankingService, RegisterService, TicTacToeFrame, TokenValidation : 

private static final String NAME_OF_THE_URL = "http://YOUR_IP_ADDRESS:YOUR_PORT/api/...";

Some parts of the project are still incomplete, such as:

- unfinished games,

- missing or limited error handling in some areas.

Rest will be developed further in future.

