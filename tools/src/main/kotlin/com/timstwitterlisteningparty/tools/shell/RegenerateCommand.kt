package com.timstwitterlisteningparty.tools.shell

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


/**
 * Calls the 'replay' and then 'allhtml' commands.
 * <br>
 * Effectively regenerates the html snippets via the [AllHtmlCommand.allhtml] and before that
 * updates the data/time-slot-data.csv with any replay links via the [CsvEnrichCommand.timeSlotFileReplayLink]
 * and then the pages/list/collection_nn.html files via [collectionPagesGenerateCommand]
 */
@ShellComponent
class RegenerateCommand(val allHtmlCommand: AllHtmlCommand, val enrishCsvCommand: CsvEnrichCommand,
                        val collectionPagesGenerateCommand: CollectionPagesGenerateCommand,
                        val timeSlotJsonGenerateCommand: TimeSlotJsonGenerateCommand) {

  @ShellMethod("Regenerates the site calling the command 'replay', 'allhtml', 'json' and 'collections' commands")
  fun regen(): String {
    val updatedTimeSlotData = enrishCsvCommand.enrich()
    val allHtml = allHtmlCommand.allhtml()
    val jsonLength = timeSlotJsonGenerateCommand.json()
    return "regen: ${updatedTimeSlotData.split("\n").count()} rows in time-slot-data and allHtml length ${allHtml.length} and collections generated = ${collectionPagesGenerateCommand.collections()} plus json files (put items * 25)  $jsonLength"
  }
}
