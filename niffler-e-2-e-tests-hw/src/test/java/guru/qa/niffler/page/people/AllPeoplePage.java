package guru.qa.niffler.page.people;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.conditions.SelenideCondition.child;

@Slf4j
@NoArgsConstructor
public class AllPeoplePage extends PeoplePage<AllPeoplePage> {

    public AllPeoplePage(boolean checkPageElementVisible) {
        super(checkPageElementVisible);
    }

    private SelenideElement allPeopleTableContainer = $("#all").as("['All people' table]");
    private final ElementsCollection allPeopleList = allPeopleTableContainer.$$("tr").as("'All people' list");

    public FriendsPage switchToFriendsTab() {
        log.info("Switching to 'Friends' tab");
        friendsTab.click();
        return new FriendsPage(true);
    }

    private AllPeoplePage sendFriendRequestToUser(String username) {
        log.info("Send friend request to user = [{}]", username);
        allPeopleList.findBy(child(usernameSelector, exactText(username))).$(addFriendButtonSelector)
                .as("['Add friend' button of user [" + username + "]]").shouldBe(visible).click();
        return this;
    }

    public AllPeoplePage shouldHaveOutcomeFriendRequest(String username) {
        log.info("Check user record [{}] have status waiting confirm", username);
        allPeopleList.findBy(child(usernameSelector, exactText(username)))
                .shouldHave(child(waitingButtonSelector, exactText("Waiting...")));
        return this;
    }

    public AllPeoplePage shouldHaveStatusNonFriend(String username) {
        log.info("Check user record [{}] have status non-friend", username);
        allPeopleList.findBy(child(usernameSelector, exactText(username)))
                .shouldHave(child(addFriendButtonSelector, exactText("Add friend")));
        return this;
    }

    @Override
    public AllPeoplePage shouldVisiblePageElement() {
        log.info("Assert 'All people' page element visible on start up");
        friendsTab.shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    @Override
    public AllPeoplePage shouldVisiblePageElements() {
        log.info("Assert 'All people' page element visible on start up");
        friendsTab.shouldHave(attribute("aria-selected", "true"));
        return this;
    }


}
