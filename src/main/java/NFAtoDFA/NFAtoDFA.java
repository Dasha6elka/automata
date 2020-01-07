package NFAtoDFA;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NFAtoDFA {
    public static void main(String[] args) {
        File input = new File("NFAinput.txt");
        try (FileInputStream inputStream = new FileInputStream(input)) {
            Scanner scanner = new Scanner(inputStream);

            List<List<String>> allStates = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] arrayStr = line.split(" ");

                List<String> list = new ArrayList<>();
                Collections.addAll(list, arrayStr);
                allStates.add(list);
            }

            Map<String, List<String>> matrixOut = new TreeMap<>(Comparator.naturalOrder());

            if (!allStates.isEmpty()) {
                int noOfElementsInList = allStates.get(0).size();
                for (int i = 0; i < noOfElementsInList; i++) {
                    List<String> col = new ArrayList<>();
                    for (List<String> row : allStates) {
                        col.add(row.get(i));
                    }
                    matrixOut.put("q" + i, col);
                }
            }

            Map<String, List<String>> map = new TreeMap<>(Comparator.naturalOrder());
            Map<String, List<String>> map1 = new TreeMap<>(map);
            matrixOut.forEach((s, strings) -> {
                if (map.isEmpty()) {
                    map.put(s, strings);
                }
            });
            fillMap(matrixOut, map, map1);

            List<List<String>> result = new ArrayList<>();
            map.forEach((s, strings) -> {
                result.add(strings);
            });

            List<List<String>> res = new ArrayList<>();
            int d = result.get(0).size();
            for (int i = 0; i < d; i++) {
                List<String> col = new ArrayList<>();
                for (List<String> row : result) {
                    col.add(row.get(i));
                }
                res.add(col);
            }

            res.forEach(strings -> {
                strings.forEach(s -> {
                    System.out.print(s);
                    System.out.print(" ");
                });
                System.out.println();
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean compareMaps(Map<String, List<String>> map1,
                                       Map<String, List<String>> map2) {
        return map1.entrySet().containsAll(map2.entrySet())
            && map2.entrySet().containsAll(map1.entrySet());
    }

    private static void fillMap(Map<String, List<String>> matrixOut, Map<String, List<String>> map, Map<String, List<String>> map1) {
        map1.putAll(map);
        map1.forEach((source, strings) -> {
            strings.forEach(value -> {
                if (value.equals("-")) {
                    return;
                }
                if (!map.containsKey(value)) {
                    if (matrixOut.containsKey(value)) {
                        matrixOut.forEach((s2, strings1) -> {
                            if (value.equals(s2)) {
                                map.put(value, strings1);
                            }
                        });
                    } else if (map.containsKey(value)) {
                        map.forEach((s2, strings1) -> {
                            if (value.equals(s2)) {
                                map.put(value, strings1);
                            }
                        });
                    } else {
                        List<String> strs = new LinkedList<>();
                        AtomicInteger i = new AtomicInteger();
                        matrixOut.forEach((key, strings1) -> {
                            i.getAndSet(0);
                            if (value.contains(key)) {
                                strings1.forEach(s3 -> {
                                    if (strs.isEmpty()) {
                                        for (int j = 0; j < strings1.size(); j++) {
                                            strs.add(j, "");
                                        }
                                    }
                                    String s4;
                                    int j = i.get();
                                    boolean flag = strs.get(j).equals("");
                                    if (flag) {
                                        s4 = s3;
                                    } else {
                                        s4 = strs.get(i.get());
                                        if (!s4.contains(s3) && !s3.contains(s4)) {
                                            if (!s3.equals("-") && !s4.equals("-")) {
                                                s4 = s4.concat(s3);
                                                Set<String> set4 = new HashSet<>(Arrays.asList(s4.split("q")));
                                                s4 = String.join("q", set4);
                                            } else if (!s3.equals("-")) {
                                                s4 = s3;
                                            }
                                        } else {
                                            if (s4.length() < s3.length()) {
                                                s4 = s3;
                                            }
                                        }
                                    }
                                    strs.set(i.get(), s4);
                                    i.incrementAndGet();
                                });
                            }
                        });
                        map.put(value, strs);
                    }
                }
            });
        });
        if (!compareMaps(map, map1)) {
            fillMap(matrixOut, map, map1);
        }
    }
}
