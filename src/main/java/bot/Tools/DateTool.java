package bot.Tools;

import java.time.LocalDateTime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateTool {

	
	 @Tool(description = "Devuelve la hora actual.")
	    String getCurrentDateTime() {
	        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
	    }

}
