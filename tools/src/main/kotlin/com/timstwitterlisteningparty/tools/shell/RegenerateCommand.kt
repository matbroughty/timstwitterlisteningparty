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
                        val collectionPagesGenerateCommand: CollectionPagesGenerateCommand) {

  @ShellMethod("Regenerates the site calling the command 'replay', 'allhtml' and 'collections' commands")
  fun regen(): String {
    val updatedTimeSlotData = enrishCsvCommand.enrich()
    val allHtml = allHtmlCommand.allhtml()
    return "regen $updatedTimeSlotData and allHtml length ${allHtml.length} and collections generated = ${collectionPagesGenerateCommand.collections()}"
  }
}
