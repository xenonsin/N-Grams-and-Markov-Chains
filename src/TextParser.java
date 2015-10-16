

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;



/**
 * Created by Keno on 8/24/2015.
 * inspiration for word frequency https://gist.github.com/EmilHernvall/953748#file-wordcount-java-L43
 * inspiration for ngram http://stackoverflow.com/questions/3656762/n-gram-generation-from-a-sentence
 */
public class TextParser extends JFrame
{

    private JLabel lTitle;
    private JLabel tfWordCount;
    private JLabel tfMostUsedWord;
    private JLabel tfLongestWord;
    private JLabel tfDistinctWordCount;
    private JButton bNewBook;

    private JTextArea taUnigramFrequency;
    private JTextArea taBigramFrequency;
    private JTextArea taTrigramFrequency;

    private JLabel taUnigramPerplexity;
    private JLabel taBigramPerplexity;
    private JLabel taTrigramPerplexity;

    private JTextArea taUnigramSample;
    private JTextArea taBigramSample;
    private JTextArea taTrigramSample;

    private String SENTENCE_END_TOKEN = " KLK ";

    private ArrayList<String> wordsArray = new ArrayList<>();
    private ArrayList<Word> sortedWordsByFrequencyArray = new ArrayList<>();
    Map<String, ArrayList<String>> unigramMap = new HashMap<>();
    Map<String, Word> unigramCountMap = new HashMap<>();


    private ArrayList<String> bigramArray = new ArrayList<>();
    private ArrayList<Word> sortedBigramByFrequencyArray = new ArrayList<>();
    Map<String, ArrayList<String>> bigramMap = new HashMap<>();
    Map<String, Word> bigramCountMap = new HashMap<>();


    private ArrayList<String> trigramArray = new ArrayList<>();
    private ArrayList<Word> sortedTrigramByFrequencyArray = new ArrayList<>();
    Map<String, ArrayList<String>> trigramMap = new HashMap<>();
    Map<String, Word> trigramCountMap = new HashMap<>();


    private JFileChooser fc;

    public static void main(String[] args)
    {
        TextParser frame = new TextParser();
        frame.setSize(700,700);
        frame.setTitle("Counting Words and Stuff. Guaranteed.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
        frame.setLayout(new GridLayout(3,3,10,10));
    }

    public TextParser()
    {
        fc = new JFileChooser();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        bNewBook = new JButton("Get New Book");
        bNewBook.addActionListener(e -> GetNewBook());
        panel.add(bNewBook);

        lTitle = new JLabel("Title: ");
        panel.add(lTitle);

        tfWordCount = new JLabel("Word Count: ");
        panel.add(tfWordCount);

        tfDistinctWordCount = new JLabel("Distinct Word Count: ");
        panel.add(tfDistinctWordCount);

        tfMostUsedWord = new JLabel("Most Used Word: ");
        panel.add(tfMostUsedWord);

        tfLongestWord = new JLabel("Longest Word: ");
        panel.add(tfLongestWord);

        add(panel);

        JPanel blank = new JPanel();
        blank.setLayout(new BoxLayout(blank, BoxLayout.Y_AXIS));
        JLabel lung = new JLabel("Lung Luong");
        blank.add(lung);
        JLabel kim = new JLabel("Kim Nibungco");
        blank.add(kim);
        add(blank);
        JPanel blank2 = new JPanel();
        blank2.setLayout(new BoxLayout(blank2, BoxLayout.Y_AXIS));
        JLabel keno = new JLabel("Keno San Pablo");
        blank2.add(keno);
        JLabel byron = new JLabel("Byron Phung");
        blank2.add(byron);
        add(blank2);
        //Frequencies

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        JLabel label2= new JLabel("Unigram Frequency");
        panel2.add(label2);
        taUnigramPerplexity = new JLabel("Unigram Perplexity:");
        panel2.add(taUnigramPerplexity);
        taUnigramFrequency = new JTextArea ();
        taUnigramFrequency.setEditable(false);
        JScrollPane scroll = new JScrollPane (taUnigramFrequency,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel2.add(scroll);
        add(panel2);


        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        JLabel label3= new JLabel("Bigram Frequency");
        panel3.add(label3);
        taBigramPerplexity = new JLabel("Bigram Perplexity:");
        panel3.add(taBigramPerplexity);
        taBigramFrequency = new JTextArea ();
        taBigramFrequency.setEditable(false);
        JScrollPane scroll3 = new JScrollPane (taBigramFrequency,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel3.add(scroll3);
        add(panel3);

        JPanel panel4 = new JPanel();
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
        JLabel label4= new JLabel("Trigram Frequency");
        panel4.add(label4);
        taTrigramPerplexity = new JLabel("Trigram Perplexity:");
        panel4.add(taTrigramPerplexity);
        taTrigramFrequency = new JTextArea ();
        taTrigramFrequency.setEditable(false);
        JScrollPane scroll4 = new JScrollPane (taTrigramFrequency,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel4.add(scroll4);
        add(panel4);

        //Samples

        JPanel panel5 = new JPanel();
        panel5.setLayout(new BoxLayout(panel5, BoxLayout.Y_AXIS));
        JLabel label5= new JLabel("Unigram Sample");
        panel5.add(label5);
        taUnigramSample = new JTextArea ();
        taUnigramSample.setEditable(false);
        taUnigramSample.setLineWrap(true);
        JScrollPane scroll5 = new JScrollPane (taUnigramSample,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel5.add(scroll5);
        add(panel5);

        JPanel panel6 = new JPanel();
        panel6.setLayout(new BoxLayout(panel6, BoxLayout.Y_AXIS));
        JLabel label6= new JLabel("Bigram Sample");
        panel6.add(label6);
        taBigramSample = new JTextArea ();
        taBigramSample.setEditable(false);
        taBigramSample.setLineWrap(true);
        JScrollPane scroll6 = new JScrollPane (taBigramSample,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel6.add(scroll6);
        add(panel6);

        JPanel panel7 = new JPanel();
        panel7.setLayout(new BoxLayout(panel7, BoxLayout.Y_AXIS));
        JLabel label7= new JLabel("Trigram Sample");
        panel7.add(label7);
        taTrigramSample = new JTextArea ();
        taTrigramSample.setEditable(false);
        taTrigramSample.setLineWrap(true);
        JScrollPane scroll7 = new JScrollPane (taTrigramSample,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel7.add(scroll7);
        add(panel7);

    }

    public void GetNewBook()
    {
        int returnVal = fc.showOpenDialog(TextParser.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String[] title = file.getName().split("\\.");
            lTitle.setText("Title: " + title[0]);
            ParseUnigram(file);
            ParseBigram();
            ParseTrigram();
        }
    }

    private void ParseUnigram(File file)
    {
        wordsArray = new ArrayList<>();
        try
        {
            FileReader fr = new FileReader(file.toString());
            BufferedReader bufRead = new BufferedReader(fr);
            String temp = null;



            while ( (temp = bufRead.readLine()) != null)
            {
                temp = temp.trim();
                temp=temp.replaceAll("\\.", SENTENCE_END_TOKEN);
                temp = temp.replaceAll("\\p{P}+", " ");
                temp = temp.replaceAll("\\r\\n|\\r|\\n", " ");
                String[] tArray = temp.split(" +");
                for(String words : tArray)
                {
                    wordsArray.add(words);
                }
                //System.out.println(temp);

            }
            GetWordCount();
            GetLongestWord();
            SortWordsByFrequency(wordsArray, sortedWordsByFrequencyArray, unigramMap, unigramCountMap, 1);
            GetMostUsedWord();
            GetDistinctWordCount();
            AppendToTextArea(sortedWordsByFrequencyArray, taUnigramFrequency);
            ConstructRandomSentence(wordsArray, unigramMap, taUnigramSample, 100);
            CalculateUnigramPerplexity(wordsArray,sortedWordsByFrequencyArray,taUnigramPerplexity,"Unigram Perplexity: ");

            //printMap(unigramMap);
            //wordsArray.forEach(System.out::println);

            fr.close();
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
    }

    private void ParseBigram()
    {
        bigramArray = ngrams(2, wordsArray);
        SortWordsByFrequency(bigramArray, sortedBigramByFrequencyArray, bigramMap, bigramCountMap, 2);
        AppendToTextArea(sortedBigramByFrequencyArray, taBigramFrequency);
        ConstructRandomSentence(bigramArray, bigramMap, taBigramSample, 100);
        CalculateBiGramPerplexity(unigramCountMap, sortedBigramByFrequencyArray, taBigramPerplexity, "Bigram Perplexity: ");

        //printMap(bigramMap);
        //bigramArray.forEach(System.out::println);
    }

    private void ParseTrigram()
    {
        trigramArray = ngrams(3, wordsArray);
        SortWordsByFrequency(trigramArray, sortedTrigramByFrequencyArray, trigramMap, trigramCountMap, 3);
        AppendToTextArea(sortedTrigramByFrequencyArray, taTrigramFrequency);
        ConstructRandomSentence(trigramArray, trigramMap, taTrigramSample, 100);
        CalculateTriGramPerplexity(bigramCountMap, sortedTrigramByFrequencyArray, taTrigramPerplexity, "Trigram Perplexity: ");

        //printMap(trigramMap);
    }

    public static ArrayList<String> ngrams(int n, ArrayList<String> words) {
        ArrayList<String> ngrams = new ArrayList<String>();
        for (int i = 0; i < words.size() - n + 1; i++)
            ngrams.add(concat(words, i, i + n));
        return ngrams;
    }

    public static String concat(ArrayList<String> words,int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words.get(i));
        return sb.toString();
    }


    private void GetLongestWord()
    {
        int maxSize = 0;
        String longestWord = "";

        for(String word : wordsArray)
        {
            if (word.length() > maxSize)
            {
                maxSize = word.length();
                longestWord = word;
            }
        }

        tfLongestWord.setText("Longest Word: " + longestWord);

    }

    private void GetWordCount()
    {
        tfWordCount.setText("Word Count: " + wordsArray.size());

    }

    private void GetDistinctWordCount()
    {
        tfDistinctWordCount.setText("Distinct Word Count: " + sortedWordsByFrequencyArray.size());
    }

    private void GetMostUsedWord()
    {
       tfMostUsedWord.setText("Most Used Word: " + sortedWordsByFrequencyArray.get(0));
    }

    private void AppendToTextArea(ArrayList<Word> source, JTextArea ta)
    {
        for (Word word : source)
        {
            ta.append(word.word + " : " + word.count);
            ta.append("\n");
        }
    }

    private void SortWordsByFrequency(ArrayList<String> source, ArrayList<Word> output, Map<String, ArrayList<String>> nmap, Map<String, Word> countMap, int n)
    {


        for (int i = 0; i < source.size(); i++) {
            String word = source.get(i);

            if (i+n < source.size() - 1)
            {
                ArrayList<String> possibleList = nmap.get(word);
                if (possibleList == null) {
                    possibleList = new ArrayList<>();
                    nmap.put(word, possibleList);
                }

                possibleList.add(source.get(i + n));
            }

            Word wordObj = countMap.get(word);
            if (wordObj == null) {
                wordObj = new Word();
                wordObj.word = word;
                wordObj.count = 0;
                countMap.put(word, wordObj);
            }
            wordObj.count++;



        }

        //This is done because creating a new ArrayList dereferences the original pointer.
        //We would need to create a new list and simply copy over the values to the output.
        ArrayList<Word> temp = new ArrayList<>(countMap.values());
        Collections.sort(temp);

        for (Word w : temp)
        {
            output.add(new Word(w));
        }

        //for(Word word : output)
        //{
        //    System.out.println(word.word + " " + word.count);
        //}



    }

    public void ConstructRandomSentence(ArrayList<String> source, Map<String, ArrayList<String>> nmap, JTextArea ta, int numOfWords)
    {
        String current = GetRandomStringFromList(source);
        String output = current;
        String[] temp = current.split(" +");
        for (int i = 0; i < numOfWords / temp.length; i++)
        {
            ArrayList<String> possibleList = nmap.get(current);

            if (possibleList != null) {
                String next = GetRandomStringFromList(possibleList);
                output += " " + next;
                current = next;
            }


        }
        output=output.replaceAll(SENTENCE_END_TOKEN,"\\.");
        ta.append(output);
        //ta.append("\n");
    }

    private String GetRandomStringFromList(ArrayList<String> list)
    {
        //list.forEach(System.out::println);
        Random rand = new Random();
        //System.out.println(list.size());

        int i = rand.nextInt(list.size());
        return list.get(i);
    }

    //http://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void CalculateUnigramPerplexity(ArrayList<String> source, ArrayList<Word> sortedFrequency, JLabel label, String base)
    {
        double probability = 0;
        double perplexity = 0;
        double totalValues = source.size();

        for (Word word : sortedFrequency)
        {
            probability += Math.log(word.count/totalValues);
        }

        probability *= (-1/totalValues);
        //System.out.println(probability);

        perplexity = Math.exp(probability);

        label.setText(base + perplexity);
    }

    public void CalculateBiGramPerplexity(Map<String,Word> unigramCountMap, ArrayList<Word> biSortedFrequency, JLabel label, String base)
    {
        double probability = 0;
        double perplexity = 0;
        double totalValues = wordsArray.size();

        for (Word word : biSortedFrequency)
        {
            String [] temp = word.word.split(" +");
            int unigramCount = unigramCountMap.get(temp[0]).count;
            probability += Math.log(word.count/unigramCount);
        }

        probability *= (-1/totalValues);
        //System.out.println(probability);

        perplexity = Math.exp(probability);

        label.setText(base + perplexity);
    }

    public void CalculateTriGramPerplexity(Map<String,Word> bigramCountMap, ArrayList<Word> triSortedFrequency, JLabel label, String base)
    {
        double probability = 0;
        double perplexity = 0;
        double totalValues = wordsArray.size();

        for (Word word : triSortedFrequency)
        {
            String [] temp = word.word.split(" +");
            String out = temp[0] + " " + temp[1];
            //System.out.println(out);
            Word wordObj = bigramCountMap.get(out);

            if (wordObj != null) {
               int bigramCount = wordObj.count;

                probability += Math.log(word.count / bigramCount);
                System.out.println(word.count );

            }


        }

        probability *= (-1/totalValues);


        perplexity = Math.exp(probability);

        label.setText(base + perplexity);
    }

    public class Word implements Comparable<Word>
    {
        String word;
        int count;


        public Word(){}
        public Word(Word wd)
        {
            word = wd.word;
            count = wd.count;
        }

        @Override
        public String toString() {
            return word;
        }
        @Override
        public int hashCode()
        {
            return word.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return word.equals(((Word)obj).word);
        }

        @Override
        public int compareTo(Word b)
        {
            return b.count - count;
        }
    }

}
