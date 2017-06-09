# JetBrains dotTrace plugin for TeamCity #

This plugin provides the ability to run a build step under the [JetBrains dotTrace Command-Line Profiler](https://www.jetbrains.com/profiler/help/Performance_Profiling__Profiling_Using_the_Command_Line.html). It is integrated into all .NET-related TeamCity build runners.

[<img src="http://teamcity.jetbrains.com/app/rest/builds/buildType:(id:TeamCityPluginsByJetBrains_DotTrace_Build)/statusIcon.svg"/>](http://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityPluginsByJetBrains_DotTrace_Build)

## How to Use It ##
The main purpose of the plugin is doing performance profiling as one of the continuous integration steps.

All you need is to: <br/>
1. Write a number of integration tests that cover performance-critical functionality of your app.<br/>
2. Add a 'unit testing' build step to your build configuration in TeamCity.<br/>
3. In the build step parameters:
* Enable *Run build step under dotTrace profiler*. 
* Set performance thresholds for your tests (or any of the underlying methods). The threshold can be set as an absolute number in ms for the method's own or total (own + call subtree) time. Another option is to check the method's execution time against previous successful builds.

That's it! Once the build is run, the plugin runs the tests and checks the execution time of the specified methods. If any of the thresholds are exceeded, the build is considered failed. After running the build step, dotTrace plugin saves a performance snapshot. Analyze it in the standalone dotTrace profiler and find out the cause of performance issues.


## Installation ##

To install the plugin, put the [zip archive](http://teamcity.jetbrains.com/httpAuth/app/rest/builds/buildType:TeamCityPluginsByJetBrains_DotTrace_Build,pinned:true,status:SUCCESS,branch:master,tags:release/artifacts/content/dotTrace.zip) to the 'plugins' directory under TeamCity data directory. Restart the server. JetBrains dotTrace command-line profiler is a free separate tool that contains a self-profiling API. To be used, the JetBrains dotTrace command-line profiler is required on each TeamCity agent where it will be run.

## Implemention ##

This project contains 3 modules: 'dotTrace-server', 'dotTrace-agent', and 'dotTrace-common'. They contain code for the server and agent parts, and a common part available to both (agent and server). When implementing components for the server and agent parts, remember to update spring context files under 'main/resources/META-INF'. See [TeamCity documentation](https://confluence.jetbrains.com/display/TCDL/Developing+Plugins+Using+Maven) for details on plugin development.

## Build ##

Use the 'mvn package' command from the root project to build your plugin. The resulting package 'dotTrace.zip' will be placed in the 'target' directory. The build is configured on the [JetBrains TeamCity build server](https://teamcity.jetbrains.com/viewLog.html?buildTypeId=TeamCityPluginsByJetBrains_DotTrace_Build&buildId=lastPinned&buildBranch=%3Cdefault%3E).

## License ##

The JetBrains dotTrace plugin for TeamCity is under the [Apache License](https://github.com/JetBrains/teamcity-dottrace/blob/master/LICENSE).

## Contributors ##

- [Nikolay Pianikov](https://github.com/NikolayPianikov)
- [Alexey Totin](https://github.com/DarthWeirdo)

## Resources ##
- [Blog post on the previous plugin version](http://blog.jetbrains.com/dotnet/2015/08/27/performance-profiling-in-continuous-integration-dottrace-and-teamcity)
- [Working with dotTrace Command-Line Profiler](https://www.jetbrains.com/profiler/help/Performance_Profiling__Profiling_Using_the_Command_Line.html)
- [Download JetBrains dotTrace command-line tools](https://www.nuget.org/packages/JetBrains.DotMemoryUnit/) see Related downloads, dotTrace command-line tools
- [Using dotTrace Perfomance API](https://confluence.jetbrains.com/display/NETCOM/Using+dotTrace+Perfomance+API)
