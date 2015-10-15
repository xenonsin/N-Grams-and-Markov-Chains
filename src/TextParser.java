

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    private JTextArea taUnigramSample;
    private JTextArea taBigramSample;
    private JTextArea taTrigramSample;

    private ArrayList<String> wordsArray = new ArrayList<>();
    private ArrayList<Word> sortedWordsByFrequencyArray = new ArrayList<>();

    private ArrayList<String> bigramArray = new ArrayList<>();
    private ArrayList<Word> sortedBigramByFrequencyArray = new ArrayList<>();

    private ArrayList<String> trigramArray = new ArrayList<>();
    private ArrayList<Word> sortedTrigramByFrequencyArray = new ArrayList<>();

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
        JLabel leech = new JLabel("Lucky Guy");
        blank2.add(leech);
        add(blank2);
        //Frequencies

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        JLabel label2= new JLabel("Unigram Frequency");
        panel2.add(label2);
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
            SortWordsByFrequency(wordsArray, sortedWordsByFrequencyArray);
            GetMostUsedWord();
            GetDistinctWordCount();
            AppendToTextArea(sortedWordsByFrequencyArray,taUnigramFrequency);

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
        SortWordsByFrequency(bigramArray,sortedBigramByFrequencyArray);
        AppendToTextArea(sortedBigramByFrequencyArray, taBigramFrequency);

        //bigramArray.forEach(System.out::println);
    }

    private void ParseTrigram()
    {
        trigramArray = ngrams(3, wordsArray);
        SortWordsByFrequency(trigramArray,sortedTrigramByFrequencyArray);
        AppendToTextArea(sortedTrigramByFrequencyArray, taTrigramFrequency);
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

    private void SortWordsByFrequency(ArrayList<String> source, ArrayList<Word> output)
    {
        Map<String, Word> countMap = new HashMap<String, Word>();

        for(String word : source)
        {
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

    public void ConstructRandomSentence(ArrayList<Word> source, JTextArea ta)
    {

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
