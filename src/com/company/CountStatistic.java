package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * ${Classname}
 * 
 * Version 1.0 
 * 
 * 24.04.2017
 * 
 * Karpikova
 */
public class CountStatistic implements Runnable{

    private String fileName;
    private ResultStatistics resultStatistics;
    private Expression expression;

    public CountStatistic(String fileName, ResultStatistics resultStatistics, Expression expression) {
        this.fileName = fileName;
        this.resultStatistics = resultStatistics;
        this.expression = expression;
    }

    @Override
    public void run() {

        String allText = "";
        File file_local = new File(fileName);
        URL url = null;
        boolean itIsLocalFile = file_local.exists();
        if (itIsLocalFile){
            allText = ReadLocalFile(file_local);
        } else {
            url = FileRemoteURL(fileName);
            if (url != null) {
                allText = ReadRemoteFile(url);
            } else {
                System.out.println("File " + fileName + " is not found.");
                return;
            }
        }
        HandleText(allText);
    }

    public void HandleText(String allText) {
        List<Integer> all = new ArrayList<Integer>();
        String[] splitted = allText.split(" ");
        int num;
        for (String word : splitted) {
            if (word.equals(""))
                continue;
            num = Integer.valueOf(word);
            all.add(num);
        }
        Optional<Integer> cur_sum = all.stream().filter((nm) -> nm%2==0).reduce((x, y)-> x+y);

        synchronized (resultStatistics){
            resultStatistics.setSum(expression.sum(resultStatistics.getSum(), cur_sum.get()));
        }
          CountStatistic.PrintStatistics(resultStatistics);
    }

    public static void PrintStatistics(ResultStatistics resultStatistics) {
        System.out.println(resultStatistics.getSum());
    }


    public static String ReadRemoteFile(URL url) {
        String allText = "";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));) {
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                allText = allText.concat(inputLine).concat(" ");
            }
        } catch (IOException e) {
            System.out.println("Sorry, IOExeption problem");
            e.printStackTrace();
        }
        finally {
            return allText.trim();
        }
    }

    public static URL FileRemoteURL(String fileName) {
        URL url = null;
        try {
            url = new URL(fileName);
        } catch (MalformedURLException e) {
            System.out.println("Ooops, we have a MalformedURLException, sorry.");
            e.printStackTrace();
            return null;
        }

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return url;
            }
            else {
                return null;
            }
        } catch (IOException e) {
            System.out.println("Ooops, checking remote file exsisting, we got an IOException, sorry.");
            e.printStackTrace();
            return null;
        }
        finally {
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String ReadLocalFile(File file_local) {
        String allText = "";
        try (BufferedReader reader = new BufferedReader (new FileReader(file_local));)
        {
            while (reader.ready()) {
                allText = allText.concat(" ").concat(reader.readLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("where is my file?");;
        } catch (IOException e) {
            System.out.println("Sorry, IOExeption problem");
            e.printStackTrace();
        }
        finally {
            return allText.trim();
        }
    }
}
