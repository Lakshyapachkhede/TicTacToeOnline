const { Server } = require("socket.io");
const http = require("http");
const express = require("express");
const crypto = require("crypto");

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});


users = {} // users[socket.id] = {name:"abc", image:"a", mark:"x"}
rooms = {} // rooms[room.id] = {player1 = socket.id, player2= socket.id, board = ["x", "o", ...], turn = ""}

usersOnline = 0;



app.get("/", (req, res) => {
    res.send("Hello");
})


let winCombinations = [
    [0, 1, 2], // Row 1
    [3, 4, 5],
    [6, 7, 8],
    [0, 3, 6], // Vertical
    [1, 4, 7],
    [2, 5, 8],
    [0, 4, 8], // Diagonal
    [2, 4, 6],
];

function checkWin(board) {
    for (let index = 0; index < winCombinations.length; index++) {
        const [a, b, c] = winCombinations[index];

        if (board[a] && board[a] === board[b] && board[b] === board[c]) {
            return index; // Return winning combination index
        }
    }

    let draw = true;
    for (let i = 0; i < 9; i++) {
        if (!board[i]) {
            draw = false;
        }
    }
    if (draw) {
        return -2;
    }

    return -1; // No winner
}




io.on("connection", (socket) => {

    console.log(`new client ${socket.id}`);
    usersOnline = usersOnline + 1;

    socket.on("create_user", (data) => {
        users[socket.id] = { name: data.name, image: data.image, room: "" }
        console.log(data);
        console.log(usersOnline);
    });

    socket.on("create_room", () => {
        const id = crypto.randomBytes(3).toString('hex');
        users[socket.id].mark = "x";
        users[socket.id].room = id
        rooms[id] = { player1: socket.id, player2: null, board: [], turn: "x" };
        socket.join(id);
        console.log("room created by", users[socket.id]);
        console.log("room id by", users[socket.id].name, "is", id)

        io.to(id).emit("room_created", { id: id });
    });


    socket.on("join_room", (data) => {
        let id = data.id
        if (rooms[id] == undefined) {
            io.to(socket.id).emit("error", { "error": "Room id is invalid", "event": "join_room" })

        }
        else {
            console.log("room join id by", users[socket.id].name, "is", id)
            if (rooms[id].player2 == null) {
                users[socket.id].mark = "o";
                users[socket.id].room = id

                rooms[id].player2 = socket.id;

                socket.join(id);

                console.log("room joined by", users[socket.id]);
                console.log(rooms[id])

                io.to(id).emit("user_joined", users[socket.id]);
                let data = { "player1": { id: rooms[id].player1, ...users[rooms[id].player1] }, "player2": { id: rooms[id].player2, ...users[rooms[id].player2] } }
                console.log(data);
                io.to(id).emit("start_game", data);
            }
        }
    });


    socket.on("play_move", (data) => {
        console.log("play_move", data)
        let id = data["id"]
        room = rooms[id]
        if (room != undefined) {
            console.log("play_move", room)
            room.board[data["index"]] = data["player"]

            let sendData = { "index": data['index'], "player": data["player"] }
            console.log("senddata", sendData)
            io.to(id).emit("move_played", sendData)

            room.turn = (data["player"] == 'x') ? 'o' : 'x'

            let win = checkWin(room.board)

            if (win == -2) {
                io.to(id).emit("draw")
            }
            else if (win != -1) {
                let data = { "winner": room.board[winCombinations[win][0]], "start": winCombinations[win][0], "end": winCombinations[win][2] }
                console.log(data);
                io.to(id).emit("win", data)
            }
        }



    });


    socket.on("disconnect", () => {
        console.log("User Disconnected", users[socket.id])

        io.to(users[socket.id].room).emit("user_disconnect", users[socket.id]);

        delete rooms[users[socket.id].room]

        delete users[socket.id]

        usersOnline = usersOnline - 1;
        console.log(usersOnline);

    });


    socket.on("reset", (id) => {
        let room = rooms[id]

        if (room != undefined) {
            room.board = []
            room.turn = 'x'

            io.to(id).emit("reset")
        }



    })



});





const IP = "192.168.30.37"

server.listen(3000, IP, () => {

    console.log(`server running at ${IP}:3000`);

});



