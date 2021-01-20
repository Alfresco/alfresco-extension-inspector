# How to contribute


## Getting started
What you need to get started:

* A [Jira](https://issues.alfresco.com/jira/projects/MNT/issues/?filter=allopenissues) account;

* A [Github](https://github.com/Alfresco) account.

Useful but not necessary:

* An [Alfresco community](https://community.alfresco.com/) account - this is a good place to ask questions and find answers. 

## Making changes

We use Jira to track issues. If you are committing a fix for a raised issue please include the ticket number in both the merge requests and the git commit messages.
For example a fix for the ticket [ALF-21953](https://issues.alfresco.com/jira/browse/ALF-21953) might have the git 
commit message: "ALF-21953 Updated to use super pom v9".

If you are adding in a new feature or bug fix please do so [here](https://issues.alfresco.com/jira/projects/MNT/issues/?filter=allopenissues). By raising a ticket in this project you will be agreeing to the 
Alfresco Contribution Agreement which can be found at the bottom of the 'Create Issue' form or alternatively attached to [this](https://community.alfresco.com/docs/DOC-7070-alfresco-contribution-agreement) page.

When you are ready to make a change you just need to fork the [glacier-connector](https://github.com/Alfresco/glacier-connector.git) 
repository and then make your changes into your copy of the code.

We have a set of standards we follow when writing code. These can be found [here](https://community.alfresco.com/docs/DOC-4658-coding-standards).

When formatting your change please try not to change the format of any other code as this can make the changes difficult to spot and please make sure to use the correct line ending (we use LF).

We ask that when adding/changing code you also add/change the appropriate unit tests and that these tests all run before creating the pull request (these will be run as part of the request, it just saves time if you know they will pass beforehand). 

If you are adding any user facing strings be advised these may be change after being reviewed by a member of our UA team. For UI consistency please write to these [guidelines](http://docs.alfresco.com/sites/docs.alfresco.com/files/public/docs_team/u2/Alfresco-Writing-Guide.pdf).

## Additional info and links

[Alfresco coding standards](https://community.alfresco.com/docs/DOC-4658-coding-standards)

[Alfresco contribution agreement](https://community.alfresco.com/docs/DOC-7070-alfresco-contribution-agreement)

[Alfresco writing guidelines](http://docs.alfresco.com/sites/docs.alfresco.com/files/public/docs_team/u2/Alfresco-Writing-Guide.pdf)

[Git "Alfresco Extension Inspector" code repository](https://github.com/Alfresco/alfresco-extension-inspector)

[IDE settings for Eclipse and Intellij](https://github.com/Alfresco/records-management/tree/master/ide-config)

[Jira project for raising issues/features](https://issues.alfresco.com/jira/projects/MNT/issues/?filter=allopenissues)
