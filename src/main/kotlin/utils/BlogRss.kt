package utils

enum class BlogRss(
    val title: String,
    val rssUrl: String,
) {
    HYUNDAI("현대자동차그룹 TECH", "https://developers.hyundaimotorgroup.com/blog/rss"),
    WOOWA("우아한형제들 기술블로그", "https://techblog.woowahan.com/feed"),
    LINE("LINE ENGINEERING", "https://engineering.linecorp.com/ko/feed/"),
    KAKAO("카카오 기술블로그", "https://tech.kakao.com/feed/"),
    TOSS("토스 테크", "https://toss.tech/rss.xml"),
    ;

    override fun toString(): String = title
}
