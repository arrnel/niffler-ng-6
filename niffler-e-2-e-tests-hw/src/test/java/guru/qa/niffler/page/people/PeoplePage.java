package guru.qa.niffler.page.people;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PeoplePage<T> {

//    protected void moveToListFirstPage() {
//        while (previousPageButton.is(not(disabled)))
//            previousPageButton.click();
//    }
//
//    public enum PeoplePageType {FRIENDS, ALL_PEOPLE}
//
//    protected final SelenideElement friendsTab = $x("//a[./h2[text()='Friends']]").as("Friends tab"),
//            allPeopleTab = $x("//a[./h2[text()='All people']]").as("All people tab"),
//            searchInput = $("input[placeholder='Search']").as("Search input"),
//            clearSearchQueryButton = $("#input-clear").as("Clear search query button"),
//            nextPageButton = $("#page-next").as("'Next' button"),
//            previousPageButton = $("#page-previous").as("'Previous' button"),
//            friendRequestsListTitle = $("Friend requests").as("Friend requests title"),
//            friendRequestsTableContainer = $("#request").as("Friend requests table container"),
//            friendsListTitle = $(byText("My friends")).as("Friends list title"),
//            friendsTableContainer = $("#friends").as("Friends list title"),
//            allPeopleTableContainer = $(byText("#friends")).as("Friends list title");
//
//    protected final ElementsCollection usersList = $$("div[id^=simple-tabpanel] tr").as("Users list"),
//            friendRequestsList = friendRequestsTableContainer.$$("tr").as("'Friend requests' list"),
//            friendsList = friendsTableContainer.$$("tr").as("'Friends' list"),
//            allPeopleList = allPeopleTableContainer.$$("tr").as("'All people' list");
//
//    public AppHeader getHeader() {
//        return new AppHeader();
//    }
//
//    private PeoplePageType getActiveTab() {
//        return friendsTab.has(cssClass("Mui-selected"))
//                ? PeoplePageType.FRIENDS
//                : PeoplePageType.ALL_PEOPLE;
//    }
//
//    protected void switchToTab(PeoplePageType tab) {
//        if (tab != getActiveTab()) {
//            switch (tab) {
//                case FRIENDS -> friendsTab.click();
//                case ALL_PEOPLE -> allPeopleTab.click();
//            }
//            tabShouldBeActive(tab);
//        }
//    }
//
//    private void tabShouldBeActive(PeoplePageType tab) {
//        SelenideElement activeTab = switch (tab) {
//            case FRIENDS -> friendsTab;
//            case ALL_PEOPLE -> allPeopleTab;
//        };
//        activeTab.shouldHave(attribute("aria-selected", "true"));
//    }
//
//    public List<String> getUserNamesList() {
//        return usersList.stream().map(SelenideElement::getText).toList();
//    }
//
//    public T filterUsersByQuery(String query) {
//        log.info("Filter users by query: [{}]", query);
//        searchInput.setValue(query).pressEnter();
//        return (T) this;
//    }
//
//    /**
//     * Filter user by username, and find username in list: friend requests, friends, all people.</br>
//     * If username found in list - return true.
//     */
//    protected Optional<SelenideElement> findUserInList(UsersListType listType, String username) {
//
//        SelenideElement listContainer = switch (listType) {
//            case REQUEST_FRIENDS -> friendRequestsTableContainer;
//            case FRIENDS -> friendsTableContainer;
//            case ALL_PEOPLE -> allPeopleTableContainer;
//        };
//
//        filterUsersByQuery(username);
//        if (listType == FRIENDS) scrollToTheTopOfFriendsTable();
//
//        var isUserFound = false;
//        while (!isUserFound && listContainer.exists()) {
//            isUserFound = listContainer.$$("tr").stream()
//                    .anyMatch(userRow -> userRow.$x(".//p[text()='" + username + "' and position()=1]").has(exactText(username)));
//            if (nextPageButton.is(not(disabled))) break;
//            if (!isUserFound) nextPageButton.click();
//
//        }
//        return isUserFound;
//    }
//
//    protected Optional<SelenideElement> getUserContainerFromListInCurrentPage(UsersListType listType, String username) {
//        ElementsCollection listContainer = switch (listType) {
//            case REQUEST_FRIENDS -> friendRequestsList;
//            case FRIENDS -> friendsList;
//            case ALL_PEOPLE -> allPeopleList;
//        };
//        return listContainer.stream().filter(userRow -> userRow.has(exactText(username))).findAny();
//    }
//
//    /**
//     * Scrolling forward table Friends pages if
//     */
//    private boolean scrollToTheTopOfFriendsTable() {
//        if (friendsTableContainer.is(not(visible))) {
//            log.info("Scroll to 'My Friends' table");
//            while (friendsTableContainer.is(not(visible)) || nextPageButton.is(not(disabled)))
//                nextPageButton.click();
//        } else {
//            while (friendRequestsTableContainer.is(not(visible)) || previousPageButton.is(not(disabled)))
//                previousPageButton.click();
//            if (friendsTableContainer.is(not(visible))) nextPageButton.click();
//        }
//        return friendsTableContainer.isDisplayed();
//    }
//
//    public List<String> findAndGetUserNamesList(String query) {
//        filterUsersByQuery(query);
//        return getUserNamesList();
//    }
//
//    protected boolean isUserExistsInCurrentPage(String username) {
//        return usersList.stream()
//                .anyMatch(userRow -> userRow.$x(".//p[text()='" + username + "' and position()=1]").has(exactText(username)));
//    }
//
//    protected enum UsersListType {
//        FRIENDS, ALL_PEOPLE, REQUEST_FRIENDS
//    }

}
