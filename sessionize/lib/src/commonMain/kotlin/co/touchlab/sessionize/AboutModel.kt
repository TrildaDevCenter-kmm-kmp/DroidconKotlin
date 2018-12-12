package co.touchlab.sessionize

import co.touchlab.sessionize.platform.backgroundSupend
import co.touchlab.stately.annotation.ThreadLocal
import co.touchlab.stately.concurrency.value
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeParser

@ThreadLocal
object AboutModel: BaseModel(AppContext.dispatcherLocal.value!!) {
    fun loadAboutInfo(proc: (aboutInfo: List<AboutInfo>) -> Unit) = launch{
        clLog("loadAboutInfo AboutModel()")
        proc(backgroundSupend { AboutProc.parseAbout() })
    }
}

internal object AboutProc{
    fun parseAbout():List<AboutInfo>{
        val aboutJsonString = AppContext.staticFileLoader("about", "json")!!
        val aboutList = ArrayList<AboutInfo>()

        val json = JsonTreeParser(aboutJsonString).readFully()

        (json as JsonArray).forEach {
            val aboutJson = it as JsonObject
            aboutList.add(
                    AboutInfo(
                            aboutJson.getPrimitive("icon").content,
                            aboutJson.getPrimitive("title").content,
                            aboutJson.getPrimitive("detail").content
                    )
            )
        }

        return aboutList
    }
}

data class AboutInfo(val icon:String, val title:String, val detail:String)