package com.timstwitterlisteningparty.tools.shell

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class ShellPromptProvider : PromptProvider {
  override fun getPrompt(): AttributedString {
    return AttributedString("ttlp:>",
      AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
  }
}
