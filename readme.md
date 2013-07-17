[![A/B Machines](http://c10566323.r23.cf2.rackcdn.com/03-28-20_derek-e-miller-and-alexis-krauss_original.jpg)](http://www.youtube.com/watch?v=DViGaee5oEs)

This is a demo application for optimizing user response to push notifications by testing two or more variations on a message. 

We use three Urban Airship APIs to get a list of iOS and Android users, send pushes to those users, and check the per-push response rates in order to perform the optimization. 

The most accessible resource for the variant of user response experiment implemented here is [this blog post](http://tdunning.blogspot.com/2012/02/bayesian-bandits.html) about Bayesian bandits. The class MessageChooserBayesBetaBinomialImpl implements the algorithm described there, but MessageChooser could be implemented to do any kind of optimization you're interested in. 

Usage
-----
There are some parameters to know about. 

ua.ab.messagesFile: The path to a file with your messages. You can have as many as you like. The file format is as follows: a line that begins "title:" contains a short title that's not sent to users, and is followed by any number of lines containing the message (newlines are preserved). You can see an example of this in resources. 

ua.ab.appKey: Gotta have one of these. 

ua.ab.masterSecret: These too. 

ua.ab.batchSize: How many pushes to send at once. There isn't a ton of reason not to set this to 1 and just let the pushes trickle out, but it's up to you. 

ua.ab.hoursToUse: How many hours to spread your pushes out over. The longer you use, the more time for users to provide feedback and optimize later pushes. The delivery schedule is basically, "Determine the number of batches by dividing the total audience by the batch size. Divide the number of milliseconds in hoursToUse by this, to get a wait time. Every that-often, send a batch." 

ua.ab.devicesSource: Use the string API to get a full list from the UA API, or the path to a file containing push addresses to use those. The file format is one push address per line, followed by white space and either 'ios' or 'android', as appropriate. 

com.urbanairship.sleighbells.agnostic.AbTestRunner is the class with the 'main' you want to run. 

Graphs
------
The graphs directory contains a script for plotting the sends, opens, and probability of leading to an open for each message over time. 
The plots in our blog post were generated with this script. 
It reads the logging output from AbTestRunner (so you'll need to have saved taht), performs some more API calls, and then uses R to spit out reasonably attractive plots. 
