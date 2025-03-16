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



io.on("connection", (socket) => {

    console.log(`new client ${socket.id}`);
    usersOnline = usersOnline + 1;

    socket.on("create_user", (data) => {
        users[socket.id] = { name: data.name, image: data.image }
        console.log(data);
        console.log(usersOnline);
    });

    socket.on("create_room", () => {
        const id = crypto.randomBytes(3).toString('hex');
        users[socket.id].mark = "x";
        rooms[id] = { player1: socket.id, player2: null, board: [], turn: "x" };
        socket.join(id);
        console.log("room created by", users[socket.id]);
        console.log("room id by", users[socket.id].name, "is", id)

        io.to(id).emit("room_created", { id: id });
    });


    socket.on("join_room", (data) => {
        let id = data.id
        if (rooms[id] == undefined) {
            io.to(socket.id).emit("error", { "error": "Room id is invalid", "event":"join_room" })

        }       
        else {
            console.log("room join id by", users[socket.id].name, "is", id)
            if (rooms[id].player2 == null) {
                users[socket.id].mark = "o";
                rooms[id].player2 = socket.id;

                socket.join(id);

                console.log("room joined by", users[socket.id]);
                console.log(rooms[id])

                io.to(id).emit("user_joined", users[socket.id]);
                let data = {"player1":{id:rooms[id].player1, ...users[rooms[id].player1]}, "player2":{id:rooms[id].player2, ...users[rooms[id].player2]}}
                console.log(data);
                io.to(id).emit("start_game", data);
            }
        }
    });


    socket.on("play_move", (id, index) => {
        room = rooms[id]
        if (room[board][index] == undefined) {
            room[board][index] = room[turn];
            room[turn] = (room[turn] == "x") ? "o" : "x";
            socket.to(id).emit("move_played", (room))
        }
    });


    socket.on("disconnect", () => {
        console.log("User Disconnected", users[socket.id])
        delete users[socket.id]
        usersOnline = usersOnline - 1;
        console.log(usersOnline);

        


    });


});





const IP = "192.168.110.37"

server.listen(3000, IP, () => {

    console.log(`server running at ${IP}:3000`);

});


