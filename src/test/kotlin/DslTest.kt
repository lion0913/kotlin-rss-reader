import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DslTest {
    @ValueSource(strings = ["이민하", "김철수"])
    @ParameterizedTest
    fun name(name: String) {
        val person: Person =
            introduce {
                name(name)
            }
        person.name shouldBe name
    }

    @Test
    fun company() {
        val person: Person =
            introduce {
                name(name)
                company("현대오토에버")
            }
        person.name shouldBe "홍길동"
        person.company shouldBe "현대오토에버"
    }

    @Test
    fun skills() {
        val person: Person =
            introduce {
                name(name)
                company("현대오토에버")
                skills {
                    soft("aa")
                    soft("bb")
                    hard("cc")
                }
            }

        person.name shouldBe "홍길동"
        person.company shouldBe "현대오토에버"
        person.skills shouldBe
            listOf(
                Soft("aa"),
                Soft("bb"),
                Hard("cc"),
            )
    }

    @Test
    fun languages() {
        val person: Person =
            introduce {
                name(name)
                company("현대오토에버")
                skills {
                    soft("aa")
                    soft("bb")
                    hard("cc")
                }
                languages {
                    "Korean" level 5
                    "English" level 3
                }
            }

        person.name shouldBe "홍길동"
        person.company shouldBe "현대오토에버"
        person.skills shouldBe
            listOf(
                Soft("aa"),
                Soft("bb"),
                Hard("cc"),
            )
        person.languages shouldBe
            hashMapOf(
                "Korean" to 5,
                "English" to 3,
            )
    }
}

private fun introduce(block: PersonBuilder.() -> Unit): Person = PersonBuilder().apply(block).build()

class PersonBuilder(
    var name: String = "홍길동",
    var company: String = "현대오토에버",
    val skills: MutableList<Any> = mutableListOf(),
    val languages: HashMap<String, Int> = hashMapOf(),
) {
    fun name(name: String) {
        this.name = name
    }

    fun company(company: String) {
        this.company = company
    }

    fun skills(block: SkillsBuilder.() -> Unit) {
        val builder = SkillsBuilder().apply(block)
        skills.addAll(builder.skills)
    }

    fun languages(block: LanguageBuilder.() -> Unit) {
        val builder = LanguageBuilder().apply(block)
        languages.putAll(builder.languages)
    }

    fun build() = Person(name, company, skills, languages)
}

data class Person(val name: String = "", val company: String = "", val skills: List<Any>, val languages: Map<String, Int>)

class LanguageBuilder {
    val languages = HashMap<String, Int>()

    infix fun String.level(value: Int) {
        languages[this] = value
    }
}

class SkillsBuilder {
    val skills: MutableList<Any> = mutableListOf()

    fun soft(name: String) {
        skills.add(Soft(name))
    }

    fun hard(name: String) {
        skills.add(Hard(name))
    }
}

data class Soft(val skill: String)

data class Hard(val skill: String)
