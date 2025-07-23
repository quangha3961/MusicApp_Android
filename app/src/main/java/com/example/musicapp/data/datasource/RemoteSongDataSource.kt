package com.example.musicapp.data.datasource

import com.example.musicapp.data.model.Song

class RemoteSongDataSource : SongDataSource {
    override fun getSongs(): List<Song> {
        return listOf(
            Song(
                "301",
                "Lạc Trôi",
                "Sơn Tùng M-TP",
                "https://a128-z3.zmdcdn.me/bb505f54a9b17b9eaaef3d730e857354?authen=exp=1753467481~acl=/bb505f54a9b17b9eaaef3d730e857354*~hmac=bf87db9722a6f546643a7ca30a1f04ae",
                "https://i.imgur.com/1bX5QH6.png"
            ),
            Song(
                "302",
                "Nơi Này Có Anh",
                "Sơn Tùng M-TP",
                "https://a128-z3.zmdcdn.me/aca52348d2f4d8f1c1932c0eb909d06b?authen=exp=1753467581~acl=/aca52348d2f4d8f1c1932c0eb909d06b*~hmac=fc33ea3833178e8447d44037d09ca8c7",
                "https://i.imgur.com/2yaf2wb.png"
            ),
            Song(
                "303",
                "Em Của Ngày Hôm Qua",
                "Sơn Tùng M-TP",
                "https://a128-z3.zmdcdn.me/3b2c96887b553ed7ba809242647d5d50?authen=exp=1753467598~acl=/3b2c96887b553ed7ba809242647d5d50*~hmac=cfa2bf034178a720c2418075e30adc3b",
                "https://i.imgur.com/3GvwNBf.png"
            ),
            Song(
                "304",
                "Chúng Ta Không Thuộc Về Nhau",
                "Sơn Tùng M-TP",
                "https://a128-z3.zmdcdn.me/ca012bcbaa21d44ab7c4124003f56ca4?authen=exp=1753467349~acl=/ca012bcbaa21d44ab7c4124003f56ca4*~hmac=b5ed21892137792c1737428197d1c0aa",
                "https://i.imgur.com/4M34hi2.png"
            ),
            Song(
                "305",
                "Âm Thầm Bên Em",
                "Sơn Tùng M-TP",
                "https://a128-z3.zmdcdn.me/5462977abf4a1e38eeed8afa52fef8a2?authen=exp=1753467642~acl=/5462977abf4a1e38eeed8afa52fef8a2*~hmac=3424cb9e2f9ad8912e03ad0e167b5d56",
                "https://i.imgur.com/5tj6S7Ol.png"
            ),
            Song(
                "999",
                "SoundHelix Song 1",
                "SoundHelix",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                "https://i.imgur.com/1bX5QH6.png"
            )
        )
    }
} 