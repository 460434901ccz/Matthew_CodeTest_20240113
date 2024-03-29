package matthew.codetest.handler;

import matthew.codetest.listener.LogListener;
import matthew.codetest.listener.RequestStringTrimListener;
import matthew.codetest.model.RequestData;
import matthew.codetest.model.ResponseData;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;


/**
 * # Stage 1
 * For a given string that only contains alphabet characters a-z, if 3 or more consecutive
 * characters are identical, remove them from the string. Repeat this process until
 * there is no more than 3 identical characters sitting besides each other.
 * Example:
 * Input: aabcccbbad
 * Output:
 * -> aabbbad
 * -> aaad
 * -> d
 *
 * @author Matthew Cai
 */
public class RemoveHandlerImpl extends AbstractHandler {

    private static final Logger logger = Logger.getLogger(RemoveHandlerImpl.class.getName());

    // the volatile modifier is used to prevent instruction reordering
    private volatile static RemoveHandlerImpl handler;

    private RemoveHandlerImpl() {
    }

    public static RemoveHandlerImpl getInstance() {
        if (handler == null) {
            synchronized (RemoveHandlerImpl.class) {
                if (handler == null) {
                    RemoveHandlerImpl handlerTmp = new RemoveHandlerImpl();
                    handlerTmp.addHandleListeners(new LogListener());
                    handlerTmp.addHandleListeners(new RequestStringTrimListener());

                    handler = handlerTmp;

                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("RemoveHandlerImpl generate instance");
                    }
                }
            }
        }
        return handler;
    }

    @Override
    ResponseData handler0(RequestData requestData) {
        ResponseData responseData = new ResponseData(requestData);

        if (logger.isLoggable(Level.INFO)) {
            logger.info(requestData.getPreProcessedString());
        }
        String result = remove(requestData.getPreProcessedString());

        responseData.setOutputString(result);
        return responseData;
    }

    /**
     * This is a recursive method,
     * the entire string after the deletion of matched substrings,
     * will scan the result again and again until there is no match substrings
     * <p>
     * risk point : Recursive method need to be aware of stack overflow issues
     *
     * @param input
     * @return
     */
    private String remove(String input) {
        Matcher matcher = getPattern().matcher(input);

        //exit recursion
        if (!matcher.find()) {
            return input;
        }
        //exit recursion

        matcher.reset();
        String result = input;
        while (matcher.find()) { //find three or more consecutive identical lowercase letters, like aaa, aaaa
            String subString = matcher.group();
            //remove the matched substring
            result = result.substring(0, result.indexOf(subString))
                    + result.substring(result.indexOf(subString) + subString.length());
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.info("Process log: " + result);
        }

        return remove(result);
    }

}
