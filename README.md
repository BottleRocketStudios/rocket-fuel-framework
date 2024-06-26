# Rocket Fuel Framework

*****THIS IS A WORK IN PROGRESS*****

This project is all about the framework portion of the Rocket Fuel Framework. If you are looking for the test creation
process and 'how to use the framework', you can find
that [here](https://github.com/BottleRocketStudios/rocket-fuel-framework-example).

## Table of Contents

- [ ] Update ToC list when finished with the README
- [What is the Rocket Fuel Framework?](#what-is-the-rocket-fuel-framework)
- [Getting Started](#getting-started)
- [Features and Integrations](#features-and-integrations)
- [Sounds Great! But How Do I Dive in?](#sounds-great-but-how-do-i-dive-in)
    - [Prerequisites](#prerequisites)
    - [Lifting off (Getting your first run)](#lifting-off-getting-your-first-run)
    - [Contributing](#contributing)
    - [Versioning](#versioning)
    - [Authors](#authors)
    - [License](#license)
    - [Acknowledgments](#acknowledgments)

## What is the Rocket Fuel Framework?

The Rocket Fuel Framework is a mostly Java-based automation framework that is designed to accelerate the creation of QA
automation tests. It can be overwhelming to find an ideal structure for you and your team to build automated tests.
Sure the page object model (POM) is a great start, and you will find familiar principles from that here, but what about
all the areas that POM doesn't cover? Developing a test creation process for multiple different platforms, devices, and
applications can be a daunting
task, whether for a single person or project, or many. You may wonder, how do I maximize common features and flows
across each platform under test?
How do I minimize time spent on maintenance when tests inevitably change, and what's the best way to grow my test suite
as my application grows?
How much time do I have to spend adding in reporting, integration with Sauce Labs, and other features? What about when I
get to an edge case like needing a
code from an email, or setting up test cases using API calls, or verifying data using a database?

The Rocket Fuel Framework (RFF) is not a specific Appium or Selenium wrapper. It is also not simply a way to create and
maintain tests.
Nor is it only capable of making API and database calls, either to test those entities or to set up data for other
tests.
Instead, RFF is a rich toolbox that puts an emphasis on being versatile, easy to use, maintainable, and to allow for
code reuse across different platforms while allowing you to focus on one single codebase.

RFF is meant to be your blueprint to establish an easy, repeatable, scalable and maintainable process that provides a
set of tools to tackle whatever test you are trying to create. Many common problems such as grabbing a code from an
email, setting up
test cases using API calls, data verification using a database, and more have all been tackled numerous times before
using this framework.

Further, RFF makes it easy for you to create automated tests for a single platform like iOS, and easily reuse code to
test similar
flows and features on other platforms like Android and Web, while not having to start from scratch. All the while
allowing you to integrate with
the other framework pieces. Need to sync your UI tests with calls to an API? No problem. Need to run your tests on Sauce
Labs? Easy.
Need to monitor network traffic? Done. Need to add some crazy feature that no one has thought of yet?
The framework is easily extensible to add in whatever feature you need, and then can be used in conjunction with the
other features of the framework.

We've taken all of our experiences working on dozens of automation projects, and distilled our learnings down into this
framework, so that you don't have to solve the same problems we'd had to solve many times over. Use this framework as
the culmination of community
knowledge and efforts from a tribe of engineers across many disciplines as a springboard to go create your own magic.

Welcome to the Rocket Fuel Framework.

## Getting Started

[Woah, this is too many words, I just want to do cool stuff](#sounds-great-but-how-do-i-dive-in)

This framework is built primarily using Java, and aims to provide a versatile, easy to use, maintainable, and
reusable toolbox for writing QA automation tests.
You can use this framework to write tests for both mobile and web applications, as well as making HTTP requests, sending
shell commands, and more.

There is a well, defined structure for writing automation tests which has been the primary focus of this framework.
Following the suggested approach will result in a maintainable and scalable test suite, that will allow for supporting
multiple platforms
and devices with minimal effort, while allowing for code reuse if the application is sufficiently similar across
platforms. The framework itself is the engine, while the test creation process is the assembly line which puts the
engine in the car
and gets it running. The test creation process is the blueprint for how to use the framework, and can be
found [here](https://github.com/BottleRocketStudios/rocket-fuel-framework-example).

If there is a feature that you are interested in but are unsure of
how to use or unable to find an example, please reach out to us, or dive into the codebase and see if you can find the
answer there.

You should be able to change the version you are using in the project
build.gradle

## Features and Integrations

Each time we came across a new problem or a new way we needed to test, we added a new feature to the framework,
integrated a new library to solve it,
or created a new process to streamline development. Now while you won't find our device rotating machine (we did
actually create one, and no it's not exactly practical)
in the framework, you will find a lot of other features that we have found to be useful in our automation projects.

Tools that have been used in the framework successfully:

- Selenium
- Appium
- TestNG
- JUnit
- Extent Reports
- Gradle Reports
- Sauce Labs
- MITM Proxy
- GitHub Actions (Your project can be setup to use GHA or others to run your tests on a schedule or on a PR merge)
- and more!

Features

- Familiar POM like structure, but with unique benefits for code reuse and scalability
- Locator Strategies are easily set and changed in one place. Also, supports multiple strategies for the same element on
  one or more platforms
- Easily switch between different platforms and devices
- Powerful locator strategies which support multiple strategies for the same element on one or more platforms
- Run locally or on Sauce Labs, or add your own run target easily
- Modular, use only the pieces that you need, ignore the rest
- Easily make HTTP requests, receive requests and integrate with APIs
- Extensible, add your own features and integrations easily by extending or subclassing
- Configurable, set what features that you want to use, screenshots, logging, run target, devices, and more!
- Easily add in calls to APIs
- Connect to databases to verify data, setup data for tests, to clean up after tests, or to run tests that don't involve
  the UI
- Monitor network traffic. Easily see what calls are being made by your app, and verify that the correct calls are being
  made. Or get data from the network to use in your tests
- Easily add in shell commands to run on your system to integrate with other tools or to run commands in your test
  environment
- Read in data from properties files, database, excel, or add your own data source
- Select from multiple reports or add your own
- Customize what is logged, or turn off logging altogether
- MockServer (experimental), easily mock out calls to APIs to test edge cases or to test when the API is not available
- Wrapper protected: Selenium, Appium, and more are wrapped in a way that makes it easy to add in new features or to
  change behaviors. No more panicking because a feature is removed, or you need to alter the behavior of a method

While there will be efforts to maintain the accuracy of this list,
the true feature list will always be in the code base itself. So if you are curious why not reach out? Or better yet
dive in yourself and see!

### RFF Features in Detail

This is a look at a few select features in the framework and how they can be used.

****THIS IS A WORK IN PROGRESS****

#### Locator Strategies

#### Configurations

#### Switching Platforms and Devices

#### Handling Multiple Platforms

#### Data Loading

#### API Calls

#### Database Calls

#### Network Traffic Monitoring

#### Shell Commands

#### Reports

## Sounds Great! But How Do I Dive in?

If you are looking for how to build fully-featured automation test suites,
an example project you can start from can be
found [here](https://github.com/BottleRocketStudios/rocket-fuel-framework-example),
Instead, if you are coming here to learn about the framework itself, or you want to make a change to utilize in your
project,
you are in the right place.

It can seem like a lot at first, and if that's you, try to just start with the features that are most interesting to
you.
There's a lot of features in RFF, but you don't have to try them all at once!

### Prerequisites

TLDR:

- Java 15 or higher
- IntelliJ IDEA (recommended)
- Mac, Linux, or Windows (Mostly used on Mac but shouldn't be an issues on other systems)
- One or more of the following depending on what you are testing:
    - Drivers (web)
    - Appium (local mobile)
        - Simulators
        - Real devices
    - Sauce Labs/other (remote execution)
- Gradle command line (optional)
- Github account and agree to the CLA (if you wish to contribute)
- A desire to learn and grow while also making your life easier and writing amazing automation tests

While we have run this framework on many different systems over the years, due to being a previously internal tool,
there
has been limited experience using it operating systems other than Mac. That's not to say it can't work, but there could
potentially
be issues
that we haven't seen before. If you are using something other than Mac, please reach out to us if you run into any
issues.

Outside of that, you will need Java on your system. Newer versions of Java are recommended, but the framework should
work on anything 15 or higher.

We highly recommend you use IntelliJ IDEA as your IDE, as it is the IDE that we have used to develop the framework,
and in our opinion it is an easy-to-use IDE with industry leading features. Of course, you are free to use any IDE that
you are comfortable with. I will say however, I have challenged everyone I've gotten set up to use IntelliJ, and they
have all come back to me
saying they love it. I've never had anyone change back to their old IDE after trying it. /end unsolicited plug for
IntelliJ

You will also need a way to run your tests. If you are testing web, you can run your tests locally and will just need to
acquire the corresponding driver for the browser you are using, assuming it is not included with the browser itself.
Due to the number of drivers, the number of systems to support, and the overhead
of updating it, you will likely need to provide your own. Perhaps in time we will have the drivers set in gradle but
for now, you will need to provide your own, or feel free to update the gradle and submit a PR to help others and
yourself :).

If you are testing mobile, you won't get very far if you can't run your code on anything. If running locally, you will
basically need to follow all the appium setup steps
[here](https://appium.io/docs/en/2.0/quickstart/) to get your simulators or real devices set up, as well as
appium and all of its requirements. We have done this plenty of times so again feel free to reach out if you get stuck.
Most of it's straightforward but there are a few gotchas that we can help with. Of course there's also a large Appium
community out there as well that might be able to help you quicker, so feel free to reach out there as well.
IOS is particularly tricky and often requires a little bit of black magic to work properly. It's much easier
to get running on a system like SauceLabs or a similar service, but that will require an account and are generally not
free.

### Lifting off (Getting your first run)

As mentioned previously there are essentially two components to the RFF. The framework itself, and the test creation
process.
You will need a project to run the tests in, and you can use the example project to get started. The framework is a jar
file of common features that can be used across multiple projects, while the test creation process is a set of
guidelines and
examples to smooth the process of creating, scaling, and maintaining your tests.

To get started, clone the example project. You can find the example
project [here](https://github.com/BottleRocketStudios/rocket-fuel-framework-example)

If you are looking to test functionality of the framework itself,
you [check out this package](src/test/java/com/bottlerocket/frameworkTests).
This is what we use to test the framework itself, and you can use it to see how the framework works, or to test new
features that you add.
Just note, for ease of use we have most of our tests set to run on Sauce Labs, so you will need to set up your own Sauce
Labs account to run them, or change the target in the test to run locally.

## Framework Structure

## Packaging the Framework for Use in Your Project

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull
requests.

## Versioning

Your project using the framework can be versioned however you like, but the framework itself will be versioned
with the following format:

`<major>.<minor>.<patch>`

Where:

- Major: Incremented when there are breaking changes
- Minor: Incremented when new features are added
- Patch: Incremented when bugs are fixed

These are not hard and fast rules, but we do our best. Generally major versions are rare and the only ones that are
allowed to break backwards compatibility.
Minor versions are more common and generally add new features or improve existing ones. Patch versions are the most
common and are generally bug fixes or small improvements.

## License

Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/

## Acknowledgments

Too many to name, but we are forever grateful to all of those of contributed, reviewed, given ideas, provided
inspiration,
tested, troubleshooted, helped with never ending PRs, documented, and more.

If you are reading this, you are likely one of those people, so thank you!
This would not have been possible without you!


