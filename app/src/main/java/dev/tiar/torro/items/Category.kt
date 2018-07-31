package dev.tiar.torro.items



/**
 * Created by Tiar on 07.2018.
 */
class Category {
    private var titleZooqle = arrayOf("Фильмы", "Сериалы", "Музыка", "Игры",
            "Программы", "Литература", "Аниме", "Другое")
    private var urlZooqle = arrayOf("Movies", "TV", "Music", "Games",
            "Apps", "Books", "Anime", "Other")
    private var titleZooqleSort = arrayOf("По сидам ↓", "По дате ↓", "По размеру ↓")
    private var urlZooqleSort = arrayOf("&s=ns&sd=d", "&s=dt&sd=d", "&s=sz&sd=d")
    //-----------------------------------------------------------------
    private var titleBitru = arrayOf("Все категории", "Фильмы", "Сериалы", "Музыка", "Игры",
            "Программы", "Литература", "Аудиокниги", "Видео", "Изображения", "XXX")
    private var urlBitru = arrayOf("", "movie", "serial", "music", "game",
            "soft", "literature", "audiobook", "video", "image", "xxx")
    private var titleBitruSort = arrayOf("По сидам ↓", "По пирам ↓", "По размеру ↓")
    private var urlBitruSort = arrayOf("&sort=seeders", "&sort=leechers", "&sort=size")
    //-----------------------------------------------------------------
    private var titleTpb = arrayOf("Аудио", "Видео", "Программы", "Игры",
            "XXX", "Другое")
    private var urlTpb = arrayOf("100", "200", "300", "400",
            "500", "600")
	//-----------------------------------------------------------------
    private var titleRutor = arrayOf("Зарубежные фильмы", "Наши фильмы",
			"Научно-популярные фильмы", "Сериалы", "Телевизор", "Мультипликация", "Аниме",
			"Музыка", "Игры", "Программы", "Спорт и Здоровье", "Юмор", "Хозяйство и Быт", "Книги",
			"Другое")
    private var urlRutor = arrayOf("1", "5", "12", "4", "6", "7", "10", "2", "8",
			"9", "13", "15", "14", "11", "3")
	private var titleRutorSort = arrayOf("По дате ↓", "По сидам ↓", "По пирам ↓", "По размеру ↓", 
			"По названию ↓")
    private var urlRutorSort = arrayOf("0", "2", "4", "8", "6")
    //-----------------------------------------------------------------
    private var titleFileek = arrayOf("Фильмы", "Музыка", "Программы", "Игры", "Книги", "Документы")
    private var urlFileek = arrayOf("cinema", "music", "soft", "games", "books", "docs")
    private var titleFileekSort = arrayOf("До 500Мб", "500Мб - 1Гб", "1Гб - 3Гб", "3Гб - 7Гб",
            "Более 7Гб")
    private var urlFileekSort = arrayOf("&fs%5B%5D=1", "&fs%5B%5D=2", "&fs%5B%5D=3", "&fs%5B%5D=4", "&fs%5B%5D=5")
    //-----------------------------------------------------------------
    private var titleNnm = arrayOf("Аниме", "Видео", "Сериалы", "Программы", "Игры", "Книги", "Музыка", "Для детей",
            "Документалки/Спорт/Юмор")
    private var urlNnm = arrayOf("&c=24", "&c=14", "&c=27", "&c=21", "&c=17", "&c=18", "&c=14", "&c=26", "&c=23")
    private var titleNnmSort = arrayOf("По дате ↓", "По сидам ↓", "По пирам ↓", "По размеру ↓",
            "По названию ↓", "По рейтингу ↓")
    private var urlNnmSort = arrayOf("&o=1&s=2", "&o=10&s=2", "&o=11&s=2", "&o=7&s=2", "&o=2&s=2", "&o=15&s=2")
    //-----------------------------------------------------------------

    fun getCategoryTitle () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> titleZooqle
            Statics.curUrl == Statics.urlBitru -> titleBitru
            Statics.curUrl == Statics.urlTpb -> titleTpb
            Statics.curUrl == Statics.urlRutor -> titleRutor
            Statics.curUrl == Statics.urlFileek -> titleFileek
            Statics.curUrl == Statics.urlNnm -> titleNnm
            else -> titleZooqle
        }
    }
    fun getCategoryUrl () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> urlZooqle
            Statics.curUrl == Statics.urlBitru -> urlBitru
            Statics.curUrl == Statics.urlTpb -> urlTpb
            Statics.curUrl == Statics.urlRutor -> urlRutor
            Statics.curUrl == Statics.urlFileek -> urlFileek
            Statics.curUrl == Statics.urlNnm -> urlNnm
            else -> urlZooqle
        }
    }

    fun getSortTitle () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> titleZooqleSort
            Statics.curUrl == Statics.urlBitru -> titleBitruSort
            Statics.curUrl == Statics.urlRutor -> titleRutorSort
            Statics.curUrl == Statics.urlFileek -> titleFileekSort
            Statics.curUrl == Statics.urlNnm -> titleNnmSort
            else -> titleZooqleSort
        }
    }
    fun getSortUrl () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> urlZooqleSort
            Statics.curUrl == Statics.urlBitru -> urlBitruSort
            Statics.curUrl == Statics.urlRutor -> urlRutorSort
            Statics.curUrl == Statics.urlFileek -> urlFileekSort
            Statics.curUrl == Statics.urlNnm -> urlNnmSort
            else -> urlZooqleSort
        }
    }
}