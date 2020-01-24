/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.extracraftx.minecraft.generatorfabricmod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.extracraftx.minecraft.generatorfabricmod.terminal.Interface;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GeneratorFabricMod {

    public static final String[] SPINNER = {
        "[    ]",
        "[=   ]",
        "[==  ]",
        "[=== ]",
        "[ ===]",
        "[  ==]",
        "[   =]",
        "[    ]",
        "[   =]",
        "[  ==]",
        "[ ===]",
        "[=== ]",
        "[==  ]",
        "[=   ]"
    };
    public static final int INTERVAL = 50;

    public static final License[] LICENSES = {
        new License("No License (Copyrighted)", "All-Rights-Reserved"),
        new License("MIT", "MIT"),
        new License("Internet Systems Consortium (ISC) License", "ISC"),
        new License("BSD 2-Clause (FreeBSD) License", "BSD-2-Clause-FreeBSD"),
        new License("BSD 3-Clause (NewBSD) License", "BSD-3-Clause"),
        new License("Apache 2.0", "Apache-2.0"),
        new License("Mozilla Public License 2.0", "MPL-2.0"),
        new License("GNU LGPL 3.0", "LGPL-3.0"),
        new License("GNU GPL 3.0", "GPL-3.0"),
        new License("GNU AGPL 3.0", "AGPL-3.0"),
        new License("Unlicense", "unlicense")
    };
    public static final int LOOM_DEFAULT = 11;
    public static final int LOOM_OLD = 9;

    public static final String[] KEYWORDS = {"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", "continue"};
    public static final Pattern PACKAGE_REGEX = Pattern.compile("^([A-Za-z$_][A-Za-z0-9$_]*\\.)*[A-Za-z$_][A-Za-z0-9$_]*$");
    public static final Pattern IDENT_REGEX = Pattern.compile("^[A-Za-z$_][A-Za-z0-9$_]*$");

    public static void main(String[] args) {
        try{
            Interface prompter = new Interface();


            prompter.startSpinner("Getting Minecraft versions... ", INTERVAL, SPINNER);
            JsonArray mcVersionsData = jsonFromUrl("https://meta.fabricmc.net/v2/versions/game").getAsJsonArray();
            String[] mcVersions = new String[mcVersionsData.size()];
            int defaultMcVersion = 0;
            for(int i = 0; i < mcVersionsData.size(); i++)
                mcVersions[i] = mcVersionsData.get(i).getAsJsonObject().get("version").getAsString();
            for(int i = 0; i < mcVersionsData.size(); i++){
                if(mcVersionsData.get(i).getAsJsonObject().get("stable").getAsBoolean()){
                    defaultMcVersion = i;
                    break;
                }
            }
            prompter.finishSpinner("[done]");
            Thread.sleep(500);


            prompter.startSpinner("Getting Fabric API versions... ", INTERVAL, SPINNER);
            JsonArray apiVersionsData = jsonFromUrl("https://addons-ecs.forgesvc.net/api/v2/addon/306612/files").getAsJsonArray();
            ArrayList<ApiVersion> apiVersionsList = new ArrayList<>();
            Pattern apiRegex = Pattern.compile("\\[([^/\\]]+)(?:/.+)?\\]");
            for(int i = 0; i < apiVersionsData.size(); i++){
                JsonObject version = apiVersionsData.get(i).getAsJsonObject();
                String displayName = version.get("displayName").getAsString();
                Matcher matcher = apiRegex.matcher(displayName);
                if(matcher.find()){
                    String mcVersion = matcher.group(1);
                    boolean found = false;
                    int index = 0;
                    for(int j = 0; j < mcVersions.length; j++){
                        if(mcVersion.equals(mcVersions[j])){
                            index = j;
                            found = true;
                        }
                    }
                    if(!found)
                        index = mcVersions.length;
                    apiVersionsList.add(new ApiVersion(index, displayName));
                }
            }
            ApiVersion[] apiVersions = new ApiVersion[apiVersionsList.size()];
            apiVersionsList.toArray(apiVersions);
            Pattern apiBuildRegex = Pattern.compile("build (\\d+)");
            Arrays.sort(apiVersions, (a,b)->{
                int verA = a.mcVersion;
                int verB = b.mcVersion;
                if(verA == verB){
                    Matcher matcherA = apiBuildRegex.matcher(a.name);
                    matcherA.find();
                    int buildA = Integer.parseInt(matcherA.group(1));
                    Matcher matcherB = apiBuildRegex.matcher(b.name);
                    matcherB.find();
                    int buildB = Integer.parseInt(matcherB.group(1));
                    return buildB - buildA;
                }
                return verA-verB;
            });
            prompter.finishSpinner("[done]");
            Thread.sleep(500);


            prompter.startSpinner("Getting Yarn mapping versions... ", INTERVAL, SPINNER);
            JsonArray yarnVersionsData = jsonFromUrl("https://meta.fabricmc.net/v2/versions/yarn").getAsJsonArray();
            YarnVersion[] yarnVersions = new YarnVersion[yarnVersionsData.size()];
            for(int i = 0; i < yarnVersionsData.size(); i++){
                JsonObject version = yarnVersionsData.get(i).getAsJsonObject();
                String mcVersion = version.get("gameVersion").getAsString();
                int index = mcVersions.length;
                for(int j = 0; j < mcVersions.length; j++){
                    if(mcVersion.equals(mcVersions[j])){
                        index = j;
                        break;
                    }
                }
                int build = version.get("build").getAsInt();
                String maven = version.get("maven").getAsString();
                String name = version.get("version").getAsString();
                yarnVersions[i] = new YarnVersion(index, build, maven, name);
            }
            prompter.finishSpinner("[done]");
            Thread.sleep(500);


            prompter.startSpinner("Getting Loom versions... ", INTERVAL, SPINNER);
            Document loomData = xmlFromUrl("https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml");
            NodeList loomVersionsData = loomData.getElementsByTagName("version");
            String[] loomVersions = new String[loomVersionsData.getLength()];
            for(int i = 0; i < loomVersions.length; i++){
                loomVersions[loomVersions.length-1-i] = loomVersionsData.item(i).getTextContent();
            }
            prompter.finishSpinner("[done]");
            Thread.sleep(500);

            
            prompter.startSpinner("Getting Fabric Loader versions... ", INTERVAL, SPINNER);
            JsonArray loaderVersionsData = jsonFromUrl("https://meta.fabricmc.net/v2/versions/loader").getAsJsonArray();
            LoaderVersion[] loaderVersions = new LoaderVersion[loaderVersionsData.size()];
            for(int i = 0; i < loaderVersionsData.size(); i++){
                JsonObject version = loaderVersionsData.get(i).getAsJsonObject();
                int build = version.get("build").getAsInt();
                String maven = version.get("maven").getAsString();
                String name = version.get("version").getAsString();
                loaderVersions[i] = new LoaderVersion(build, maven, name);
            }
            prompter.finishSpinner("[done]");
            Thread.sleep(500);

            
            Pattern semver = Pattern.compile("(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?");

            int mcVersion = prompter.promptList("Minecraft version:", true, defaultMcVersion, mcVersions);
            String modName = prompter.prompt("Mod name:", s->s.isEmpty()?"You must input a name":null);
            String modId = prompter.prompt("Mod id (must be unique):", s->s.isEmpty()?"You must input a mod id":null);
            String modDescription = prompter.prompt("Mod description:", s->s.isEmpty()?"You must input a description":null);
            // String modVersion = prompter.prompt("Mod version", "0.1.0", s->{
            //     if(s.isEmpty())
            //         return "You must input a mod version";
            //     if(!semver.matcher(s).matches())
            //         return "Please ensure you use SemVer";
            //     return null;
            // });
            String modVersion = prompter.promptSemVer("Mod version:");
            String author = prompter.prompt("Author:", s->s.isEmpty()?"You must input an author":null);
            String homepage = prompter.prompt("Homepage (not required):", s->{
                if(s.isEmpty())
                    return null;
                if(!isValidUrl(s))
                    return "Please enter a valid URL";
                return null;
            });
            String sources = prompter.prompt("Source code URL (not required):", s->{
                if(s.isEmpty())
                    return null;
                if(!isValidUrl(s))
                    return "Please enter a valid URL";
                return null;
            });
            int license = prompter.promptList("License:", true, 1, LICENSES);
            String licenseName = author;
            if(license != LICENSES.length-1);
                licenseName = prompter.prompt("Name on license:", author, s->s.isEmpty()?"You must enter a name for the license":null);
            String packageName = prompter.prompt("Main package:", s->{
                if(s.isEmpty())
                    return "You must enter a package";
                if(PACKAGE_REGEX.matcher(s).matches()){
                    String[] idents = s.split("\\.");
                    for(String ident : idents){
                        if(contains(KEYWORDS, ident))
                            return ident + " is a Java keyword";
                    }
                    return null;
                }else{
                    return "Please enter a valid Java package name";
                }
            });
            String mainClass = prompter.prompt("Mod initialiser class:", s->{
                if(s.isEmpty())
                    return "You must enter an initialiser class";
                if(IDENT_REGEX.matcher(s).matches()){
                    if(contains(KEYWORDS, s))
                        return s + " is a Java keyword";
                    return null;
                }else{
                    return "Please enter a valid Java class name";
                }
            });
            boolean mixins = prompter.yesOrNo("Use mixins?", true);
            boolean useApi = prompter.yesOrNo("Use Fabric API?", true);
            int apiVersion = -1;
            if(useApi){
                int defaultApi = 0;
                for(int i = 0; i < apiVersions.length; i++){
                    if(apiVersions[i].mcVersion >= mcVersion){
                        defaultApi = i;
                        break;
                    }
                }
                apiVersion = prompter.promptList("Select Fabric API version:", true, defaultApi, apiVersions);
            }
            Object[] yarnOptions = Arrays.asList(yarnVersions).stream().filter(version->version.mcVersion == mcVersion).toArray();
            int v1_14_4 = 0;
            for(int i = 0; i < mcVersions.length; i++){
                if(mcVersions[i].equals("1.14.4")){
                    v1_14_4 = i;
                    break;
                }
            }
            int yarnVersionIndex = prompter.promptList("Select Yarn mappings:", true, 0, yarnOptions);
            YarnVersion yarnVersion = (YarnVersion) yarnOptions[yarnVersionIndex];
            boolean v2 = yarnVersion.mcVersion < v1_14_4 || (yarnVersion.mcVersion == v1_14_4 && yarnVersion.build > 14);
            int defaultLoom = loomVersions.length - (v2 ? LOOM_DEFAULT : LOOM_OLD) - 1;
            int loomVersion = prompter.promptList("Select Loom version:", true, defaultLoom, loomVersions);
            int loaderVersion = prompter.promptList("Select Fabric Loader version:", true, 0, loaderVersions);
        }
        catch(Exception e){
            // e.printStackTrace();
        }
    }

    public static Document xmlFromUrl(String urlString) throws IOException, SAXException, ParserConfigurationException {
        URL url = new URL(urlString);
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
    }

    public static JsonElement jsonFromUrl(String url) throws IOException{
        return JsonParser.parseString(getUrl(url));
    }

    public static String getUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        return readAllFromStream(url.openStream());
    }
    
    public static String readAllFromStream(InputStream stream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }
    
    public static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean contains(String[] array, String s){
        for(String item : array){
            if(s.equals(item))
                return true;
        }
        return false;
    }

    private static class ApiVersion{
        int mcVersion;
        String name;

        public ApiVersion(int mcVersion, String name){
            this.mcVersion = mcVersion;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class YarnVersion{
        int mcVersion;
        int build;
        String maven;
        String name;

        public YarnVersion(int mcVersion, int build, String maven, String name){
            this.mcVersion = mcVersion;
            this.build = build;
            this.maven = maven;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class LoaderVersion{
        int build;
        String maven;
        String name;

        public LoaderVersion(int build, String maven, String name){
            this.build = build;
            this.maven = maven;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class License{
        String name;
        String val;

        public License(String name, String val){
            this.name = name;
            this.val = val;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
