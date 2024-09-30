package guru.qa.niffler.page.people;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AllPeoplePage extends PeoplePage<AllPeoplePage> {

//    public FriendsPage switchToFriendsTab() {
//        switchToTab(PeoplePageType.FRIENDS);
//        return new FriendsPage();
//    }
//
//    private boolean isUserExistsInAllPeopleTable(String username) {
//        log.info("Find user with name {} in 'All people' table", username);
//        return findUserInList(ALL_PEOPLE, username);
//    }
//
//    private FriendStatus getUserFromCurrentPage(String username) {
//        SelenideElement userContainer = getUserContainerFromListInCurrentPage(ALL_PEOPLE, username).orElseThrow(()->new UserNotFoundException("User with username = [%s] not found".formatted(username)));
//    }
//
//    public AllPeoplePage addToFriend(String username) {
//        if (isUserExistsInAllPeopleTable(username)) {
//            switch (getUserStatus(username)) {
//                case NON_FRIEND -> ;
//                case WAITING_CONFIRMATION -> log.info("Friend request was already sent");
//            }
//        } else {
//            log.info("User with username = [{}] not found in all people table", username);
//        }
//    }
//
//    protected Optional<SelenideElement> findUser(String username) {
//        return isUserExistsInAllPeopleTable(username)
//                ? Optional.of()
//                : Optional.empty();
//    }
//
//    public AllPeoplePage addFriend(String username) {
//
//        filterUsersByQuery(username);
//        if (allPeopleList.isEmpty()) throw new UserNotFoundException("Users with name " + username + " not found");
//
//        allPeopleList.stream()
//                .filter(userContainer -> userContainer.has(exactText(username)))
//                .findFirst().orElseThrow(() -> new UserNotFoundException("User with username = [%s] in all peoples page not found".formatted(username)))
//                .click();
//        return this;
//
//    }

}
