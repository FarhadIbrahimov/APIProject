package httpClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static httpClient.HttpClientUtils.getResponseBody;

public class GotTests {

    @Test
    public void verifyEachCharacterHasID() {
        HttpResponse response =
                httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characters", null);
        List<Map<String, Object>> deserializedObj = HttpClientUtils.getResponseBodyAsList(response); // convert JSON -> Java

        HashSet<String> allIds = new HashSet<>();

        for (Map<String, Object> character : deserializedObj) {
            allIds.add((String) character.get("id"));
        }

        Assert.assertEquals(deserializedObj.size(), allIds.size());
    }

    // 1) which characters are dead
    // 2) make sure all dead characters have a placeOfDeath field
    @Test
    public void verifyDeadCharsHavePlaceOfDeathField() {
        HttpResponse response =
                httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characters", null);
        List<Map<String, Object>> deserializedObj = HttpClientUtils.getResponseBodyAsList(response); // convert JSON -> Java

        boolean failAssert = false;
        for (Map<String, Object> character : deserializedObj) {
            if (!(boolean) character.get("alive")) { // alive == false
                try {
                    Assert.assertNotEquals("Dead character '" + character.get("name") + "' doesn't have place of Death",
                            null, (String) character.get("placeOfDeath"));
                } catch (AssertionError e) {
                    e.printStackTrace();
                    failAssert = true;
                }
            }
        }
        if (failAssert) {
            throw new AssertionError("Some of characters had no place of Death while being dead");
        }
    }

    // find a list of all houses from characters book
    // find a list of all houses from houses book
    // verify names of houses in both lists match
    @Test
    public void verifyHouseNamesMatch() {
        HttpResponse response1 =
                httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characters", null);
        List<Map<String, Object>> deserializedObj1 = HttpClientUtils.getResponseBodyAsList(response1); // list of characters

        List<String> result = new ArrayList<>();
        for (Map<String, Object> character : deserializedObj1) {
            result.addAll((List<String>) character.get("allegiance"));
        }
        TreeSet<String> allHousesFromCharBook = new TreeSet<>(result);


        HttpResponse response2 =
                httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/houses", null);
        List<Map<String, Object>> deserializedObj2 = HttpClientUtils.getResponseBodyAsList(response2); // convert JSON -> Java

        TreeSet<String> allHousesFromHouseBook = new TreeSet<>();
        for (Map<String, Object> house : deserializedObj2) {
            allHousesFromHouseBook.add((String) house.get("name"));
        }

        boolean failAssert = false;
        boolean houseMatch = false;
        for (String house1 : allHousesFromCharBook) {
            try {
                houseMatch = allHousesFromHouseBook.contains(house1);
                Assert.assertFalse("This house from characters book '" + house1 + "' does not exist in houses book",
                        houseMatch); // every time houseMatch is FALSE -> print out message
            } catch (AssertionError e) {
                e.printStackTrace();
                failAssert = true;
            }
        }
        for (String house2 : allHousesFromHouseBook) {
            try {
                houseMatch = allHousesFromCharBook.contains(house2);
                Assert.assertFalse("This house from houses book '" + house2 + "' does not exist in characters book",
                        houseMatch);
            } catch (AssertionError e) {
                e.printStackTrace();
                failAssert = true;
            }
        }
        if (failAssert) {
            throw new AssertionError("Some houses in the characters book do not exist in the houses book.");
        }
    }

    // TASK 4
    // get each character name from https://api.got.show/api/book/characters endpoint
    // and make sure it can be found with exact same name while using https://api.got.show/api/book/characterlocations/:name endpoint

    @Test
    public void verifyNameEndpointCharBook() {
        HttpResponse response1 =
                httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characters", null);
        List<Map<String, Object>> deserializedObj1 = HttpClientUtils.getResponseBodyAsList(response1); // list of characters

        List<String> allCharacterNames = new ArrayList<>();
        for (Map<String, Object> character : deserializedObj1) {
            allCharacterNames.add((String) character.get("name"));
        }

        System.out.println(allCharacterNames.size());

        //boolean characterMapFound = false;
        boolean failAssert = false;
        int num = 0;
        for (String name : allCharacterNames) {
            System.out.println("Processing..." + name);
            if (num > 50) {
                break;
            }
            num++;
            HttpResponse response2 =
                    httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characters", new String[]{name});
            try {
                Assert.assertEquals("Character not found: " + name, HttpStatus.SC_OK, response2.getStatusLine().getStatusCode());
            } catch (AssertionError e) {
                e.printStackTrace();
                failAssert = true;
            }
        }
    }

    // TASK 5
    // get each character name from  https://api.got.show/api/book/characterlocations endpoint
    // and make sure it can be found with exact same name while using https://api.got.show/api/book/characterlocations/:name endpoint

    @Test
    public void verifyNameEndpointLocationsBook() {
        HttpResponse response1 =
                httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characterlocations", null);

        Map<String, Object> deserializedObject = getResponseBody(response1); // convert Json -> Java

        List<Map<String, Object>> deserializedData = ((List<Map<String, Object>>) deserializedObject.get("data"));
        //String message = (String) deserializedObject.get("message");
        //System.out.println(message);


        List<String> allCharacterNames = new ArrayList<>();
        for (Map<String, Object> character : deserializedData) {
            allCharacterNames.add((String) character.get("name"));
        }

        //boolean characterMapFound = false;
        boolean failAssert = false;
        int num = 0;
        for (String name : allCharacterNames) {
            System.out.println("Processing..." + name);
            if (num > 20) {
                break;
            }
            num++;
            HttpResponse response2 =
                    httpClient.HttpClientUtils.getGetResponse("https", "api.got.show", "api/book/characterlocations", new String[]{name});
            try {
                Assert.assertEquals("Character not found: " + name, HttpStatus.SC_OK, response2.getStatusLine().getStatusCode());
            } catch (AssertionError e) {
                e.printStackTrace();
                failAssert = true;
            }
        }
    }
}









