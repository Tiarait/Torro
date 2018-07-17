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

    private var titleBitru = arrayOf("Все категории", "Фильмы", "Сериалы", "Музыка", "Игры",
            "Программы", "Литература", "Аудиокниги", "Видео", "Изображения", "XXX")
    private var urlBitru = arrayOf("", "movie", "serial", "music", "game",
            "soft", "literature", "audiobook", "video", "image", "xxx")

    private var titleBitruSort = arrayOf("По сидам ↓", "По пирам ↓", "По размеру ↓")
    private var urlBitruSort = arrayOf("&sort=seeders", "&sort=leechers", "&sort=size")

    private var titleTpb = arrayOf("Аудио", "Видео", "Программы", "Игры",
            "XXX", "Другое")
    private var urlTpb = arrayOf("100", "200", "300", "400",
            "500", "600")
	
    private var titleRutor = arrayOf("Зарубежные фильмы", "Наши фильмы",
			"Научно-популярные фильмы", "Сериалы", "Телевизор", "Мультипликация", "Аниме",
			"Музыка", "Игры", "Софт", "Спорт и Здоровье", "Юмор", "Хозяйство и Быт", "Книги",
			"Другое")
    private var urlRutor = arrayOf("1", "5", "12", "4", "6", "7", "10", "2", "8",
			"9", "13", "15", "14", "11", "3")
	private var titleRutorSort = arrayOf("По дате ↓", "По сидам ↓", "По пирам ↓", "По размеру ↓", 
			"По названию ↓")
    private var urlRutorSort = arrayOf("0", "2", "4", "8", "6")

    fun getCategoryTitle () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> titleZooqle
            Statics.curUrl == Statics.urlBitru -> titleBitru
            Statics.curUrl == Statics.urlTpb -> titleTpb
            Statics.curUrl == Statics.urlRutor -> titleRutor
            else -> arrayOf("")
        }
    }
    fun getCategoryUrl () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> urlZooqle
            Statics.curUrl == Statics.urlBitru -> urlBitru
            Statics.curUrl == Statics.urlTpb -> urlTpb
            Statics.curUrl == Statics.urlRutor -> urlRutor
            else -> arrayOf("")
        }
    }

    fun getSortTitle () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> titleZooqleSort
            Statics.curUrl == Statics.urlBitru -> titleBitruSort
            Statics.curUrl == Statics.urlRutor -> titleRutorSort
            else -> arrayOf("")
        }
    }
    fun getSortUrl () : Array<String> {
        return when {
            Statics.curUrl == Statics.urlZooqle -> urlZooqleSort
            Statics.curUrl == Statics.urlBitru -> urlBitru
            Statics.curUrl == Statics.urlRutor -> urlRutorSort
            else -> arrayOf("")
        }
    }
}