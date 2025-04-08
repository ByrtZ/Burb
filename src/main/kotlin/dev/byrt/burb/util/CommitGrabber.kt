package dev.byrt.burb.util

import dev.byrt.burb.chat.ChatUtility
import dev.byrt.burb.chat.Formatting
import dev.byrt.burb.plugin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay

import org.json.JSONArray

import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

@Suppress("unstableApiUsage")
object CommitGrabber {
    private val latestDefaultBranchCommitURL = URL("https://api.github.com/repos/ByrtZ/Burb/commits?per_page=1")
    private val branchesURL = URL("https://api.github.com/repos/ByrtZ/Burb/branches")
    private val singleCommitURLSuffix = "?per_page=1"
    //TODO: Add ability to view commits on each branch
    fun grabLatestCommit() = runBlocking {
        for(world in Bukkit.getWorlds()) {
            for(textDisplay in world.getEntitiesByClass(TextDisplay::class.java)) {
                if(textDisplay.scoreboardTags.contains("burb.lobby.updates_display")) {
                    textDisplay.remove()
                }
            }
        }
        val commit = requestCommitData()
        ChatUtility.broadcastDev("<dark_gray>${if(commit == null) "Failed to fetch" else "Successfully fetched"} latest commit.", false)
        val commitUpdateDisplay = Bukkit.getWorlds()[0].spawn(Location(Bukkit.getWorlds()[0], -32.9375, 5.875, 0.5, -90.0f, 0.0f), TextDisplay::class.java).apply {
            alignment = TextDisplay.TextAlignment.CENTER
            billboard = Display.Billboard.FIXED
            text(Formatting.allTags.deserialize("<burbcolour><b><font:burb:font>LATEST  UPDATES<reset><newline><i><gray>${if(commit != null) "Commit ID: <yellow>${commit.first}" else "Commit ID: <red></i>None"}<reset><newline><newline><font:burb:font><i>${commit?.second?.replace(" ", "  ") ?: "</i><red>No  commit  message."}<reset><newline><newline><font:burb:font>${if(commit != null && (plugin.pluginMeta.version.contains(commit.first))) "<green>Server  up  to  date" else "<red>Server  outdated"}"))
            scoreboardTags.add("burb.lobby.updates_display")
        }
    }

    private suspend fun requestCommitData(): Pair<String, String>? {
        var connection: HttpURLConnection? = null
        return try {
            connection = withContext(Dispatchers.IO) {
                latestDefaultBranchCommitURL.openConnection()
            } as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.lines().collect(Collectors.joining()) }
                val jsonArray = JSONArray(response)
                var id = ""
                var message = ""

                for (i in 0 until jsonArray.length()) {
                    val commitObject = jsonArray.getJSONObject(i)
                    val commitDetails = commitObject.getJSONObject("commit")
                    id = commitObject.getString("sha").take(7)
                    message = commitDetails.getString("message")
                }

                return Pair(id, message)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }
}