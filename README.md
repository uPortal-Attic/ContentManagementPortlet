## Description

Content Management Portlet is developed for uPortal to allow authorized users to administer content remotely. Using this portlet, content authors 
can remotely edit and publish content independently, and pull in and share existing data and re-purpose it according to different needs. Project supports
multiple languages and further allows decoration of content with graphics, links, and templates.

## Features

Portlet will support two user categories: authors and viewers who publish and view content respectively.
Author shall use WYSIWYG editor to produce content. WYSIWYG editor shall provide navigational and keyboard support for users with disabilities.
All content will be checked and validated for XSS security issues, before any is published.
Authors currently are able to insert images, links and smiley icons into the content.
Portlet adheres to the portal theme and style.
Allow authors to choose templates for content.
Ability to upload multiple attachments along with the post, along with thumbnail views.
Ability to display video/audio attachments natively using HTML5 capabilities.
Ability to schedule posts to be published at a later date/time.
Ability to localize portlet content for multiple languages.
Allow a post to be rated by viewers.

## Build

Adjust the paths in the build.properties file (portal, tomcat and maven directories)
On the command line inside the project source directory, use the following command:
> ant package

Once the build is successful, you should be able to browse to the "<tomcat-home>/webapps" and find the "ContentManagementPortlet.war" application file.

## Report Bugs
If you have encounted an issue with the portlet, please submit an entry here: https://issues.jasig.org/browse/INC-15