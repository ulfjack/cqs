<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
%set player Session.getPlayer;
%define Colony colony;
%if Request.getFilename.startsWith("colony");
%set colony Session.getColony(Request.getUrlParameter(0));
%else;
%set colony null;
%endif;
<head>
	<title>Conquer-Space.net</title>
	<meta name="robots" content="noindex" />
%if player.advancedCSS;%= Session.getCssIncludeHtml("css/style.css", "css/new.css", "css/advanced.css");%else;%= Session.getCssIncludeHtml("css/style.css", "css/new.css");%endif;
	<meta name="x-cqs-time" id="x-cqs-time" content="%= Galaxy.getTime;" />
	<meta name="x-cqs-speed" id="x-cqs-speed" content="%= Galaxy.getSpeed;" />
	<meta name="x-cqs-running" id="x-cqs-running" content="%= Galaxy.isRunning;" />
	<meta name="x-cqs-debug" id="x-cqs-debug" content="%= Tools.debug;" />
%= Session.getJsIncludeHtml("js/common.js", "js/common-json.js", "js/common-timer.js", "js/i18n_"+Session.getLocale.toString+".js");
	<!--[if IE]>
	<link rel="stylesheet" type="text/css" href="pack/css/iefix.css" />
	<script type="text/javascript" src="js/iebugfix.js"></script>
	<![endif]-->
</head>
