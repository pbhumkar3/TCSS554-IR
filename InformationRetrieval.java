/*

Auther : Pradnya Bhumkar
TCSS 554  IR    Spring 2019
Assignment 1

This assignment extract the information about the tokens from the sample data.

*/

// Importing Libraries
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.tartarus.snowball.ext.porterStemmer;

public class InformationRetrieval {

    // Function to read the stopwords.txt file and save it in hash set.
    public HashSet<String> readStopWords()
    {
        HashSet<String> stopWords = new HashSet<>();
        String word = null;
        File stopWordsFile = new File("src/stopwords.txt");
        try {
            Scanner s = new Scanner(stopWordsFile);

            while (s.hasNext()) {
                word = s.next();
                if(stopWords.contains(word) == false)
                {
                    stopWords.add(word);
                }
                else
                {
                    System.out.println("Stop Word Already Present : " + word);
                }
                word = null;
            }
        } catch (IOException e) {
            System.out.println("Error accessing input file!");
        }

        return stopWords;
    }

    //this function reads the data from each file and save it in a list and returns the list.
    public List<String> readDataFromFile()
    {
        List<String> listOFTextFromTranscript = new ArrayList<>();

        File transcriptFolder = new File("src/transcripts");
        String currentline;

        if(transcriptFolder.exists()){
            String[] listOfFiles = transcriptFolder.list();
            for(String s:listOfFiles){
                try {

                    BufferedReader br = new BufferedReader(new FileReader(transcriptFolder.getAbsolutePath() + "/" + s.toString()));
                    String temp = "";
                        while ((currentline = br.readLine()) != null) {
                        temp = temp + currentline.toLowerCase();
                    }
                    listOFTextFromTranscript.add(temp);
                    temp = ""; //clears the temporary string each time;
                } catch (FileNotFoundException e)
                {
                    System.out.println("File Not Found " + e.getMessage());
                }
                catch (IOException e1)
                {
                 System.out.println(e1.getMessage() );
                }

            }
        }

        return listOFTextFromTranscript;
    }

    //this function counts the words tokens occurred in each document.
    //this function is used to count words before and after the data processing.
    public int countwords(List<String> result)
    {
        int count = 0;
        for(String s : result)
        {
            String Arraylist[] = s.split(" ");
            for (int i = 0; i<Arraylist.length; i++ )
            {
                if(Arraylist[i].equals(""))
                {
                    //System.out.println("Skip");
                }
                else {
                    count = count + 1;
                }
            }
            //System.out.println("Count : " + count);
        }
        return count;
    }

    /*
    This function is used to perform all data processing task.
    data present in the list is accessed one by one.
    complex words are decompose first.
    then each word is checked with stop words list and if the word is a stop word then it is removed from the original list.
    All punctuations are removed from the list.
    each word is then pass to stemmer library find the stem word.
    each word is then stored in Hash map with the count.
    */
    public Map<String,Integer> processData(List<String> result,HashSet<String> stopWords)
    {
        Map<String,Integer> list_of_words = new HashMap<String, Integer>();
        String currentLine;
        String tempWord = null;
        String tempCurrentLine = null;
        for (int i=0 ; i < result.size(); i++)
        {
            currentLine = result.get(i);
            currentLine = currentLine.replaceAll("won't", "will not");
            currentLine = currentLine.replaceAll("can't", "can not");
            currentLine = currentLine.replaceAll("n't"," not");
            currentLine = currentLine.replaceAll("'re"," are");
            currentLine = currentLine.replaceAll("'d"," would");
            currentLine = currentLine.replaceAll("'ll"," will");
            currentLine= currentLine.replaceAll("'t"," not");
            currentLine = currentLine.replaceAll("'ve"," have");
            currentLine = currentLine.replaceAll("'m"," am");
            currentLine = currentLine.replaceAll("'s"," is");
            currentLine = currentLine.toString().replaceAll("[^a-zA-Z\\s+]", "");
            String[] words = currentLine.toString().split(" ");
            tempCurrentLine = "";
            for(String s : words) {
                if (s.equals("") == false) {
                    if (stopWords.contains(s) == false) {
                        porterStemmer stemmer = new porterStemmer();
                        stemmer.setCurrent(s);
                        stemmer.stem();
                        tempWord = stemmer.getCurrent();
                        if (list_of_words.containsKey(tempWord) == true) {
                            int count = list_of_words.get(tempWord);
                            list_of_words.replace(tempWord, count + 1);
                        } else {
                            list_of_words.put(tempWord, 1);
                        }
                        tempCurrentLine = tempCurrentLine + " " + tempWord;
                    }
//                else {
//                    System.out.println("Skip : " + s.toString());
//                }
                }
            }
            result.set(i,tempCurrentLine);
        }

        return list_of_words;
    }

    //this function counts the number of words in the list of document that occurs only once.
    public int countWordOccureOnce(Map<String,Integer> list_of_words)
    {
        int count = 0;
        Iterator it = list_of_words.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().equals(1))
            {
                count = count + 1;
            }
            //System.out.println(pair.getKey() + " = " + pair.getValue());
        }
        return count;
    }

    //this function sorts the hash Map on the bases of the value.
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order)
    {
        List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    //This function displays all the information about the word token, i.e. TF,TF(Weight), IDF, TF*IDF,P(word) etc.
    public void calculateTFnIDF(List<String> result , Map<String, Integer> sorted_list_of_words, int count)
    {
        float df= 0.0f;
        float idf = 0.0f;
        float tf = 0.0f;
        Map<String,Float> docFreqMap = new HashMap<String, Float>();
            for(Map.Entry<String, Integer> entry : sorted_list_of_words.entrySet())
            {
                for (int j = 0; j<result.size(); j++)
                {
                    if (result.get(j).contains(entry.getKey()))
                    {
                        df++;
                    }
                }
                docFreqMap.put(entry.getKey(),df);
                if (docFreqMap.size() == 30)
                {
                    break;
                }
                df = 0;
            }
            System.out.println("Q5    For 30 most frequent words in the database \n");

            for(Map.Entry<String, Float> entry : docFreqMap.entrySet())
            {
                df = entry.getValue();
                idf = (float) Math.log10(result.size()/df);
                tf = sorted_list_of_words.get(entry.getKey());
                System.out.println("Word : " + entry.getKey() + "\t \t TF : " + tf + "\t \t TF(Weight) : "+ (1+Math.log10(tf)) + "\t \t DF : "+ df + "\t \t IDF :" + idf + "\t \t TF*IDF : " + (float)(tf*idf) + "\t \t Probability : " +(tf/count));
                df = 0.0f;
                idf = 0.0f;
                tf = 0;
            }


    }

    public static void main(String[] args)
    {
        InformationRetrieval IR = new InformationRetrieval();
        Map<String,Integer> list_of_words = new HashMap<String, Integer>();
        List<String> result = new ArrayList<>();
        int count = 0;
        HashSet<String> stopWords = new HashSet<>();
        stopWords = IR.readStopWords();

        result = IR.readDataFromFile();
        count = IR.countwords(result);
        System.out.println("Q1 A) The number of word tokens in the database (before processing steps) : " + count);
        list_of_words = IR.processData(result,stopWords);
        count = 0;
        count = IR.countwords(result);
        System.out.println("Q1 B) The number of word tokens in the database (After processing steps) : " + count);
        System.out.println("Q2    The number of unique words in the database : " + (int)list_of_words.size());
        System.out.println("\n    The number of Documents : " + result.size());
        System.out.println("\nQ3    The number of words that occur only once in the database : " + IR.countWordOccureOnce(list_of_words));
        System.out.println("Q4    The average number of word tokens per document : " + (float)(IR.countwords(result)/result.size()));

        Map<String, Integer> sorted_list_of_words = sortByValue(list_of_words, false);

        IR.calculateTFnIDF(result, sorted_list_of_words, count);

    }
}
