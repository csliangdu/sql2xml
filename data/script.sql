USE [he20171206]
GO
/****** Object:  User [honge]    Script Date: 12/09/2017 11:28:44 ******/
CREATE USER [honge] WITHOUT LOGIN WITH DEFAULT_SCHEMA=[honge]
GO
/****** Object:  Schema [honge]    Script Date: 12/09/2017 11:28:44 ******/
CREATE SCHEMA [honge] AUTHORIZATION [honge]
GO
/****** Object:  Table [dbo].[cms_Channels]    Script Date: 12/09/2017 11:28:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[cms_Channels](
	[ChannelId] [int] IDENTITY(1,1) NOT NULL,
	[ChannelCode] [varchar](50) NULL,
	[ChannelName] [nvarchar](50) NULL,
	[Comment] [nvarchar](500) NULL,
	[ParentId] [int] NULL,
	[ChannelTemplate] [varchar](500) NULL,
	[ChannelPage] [varchar](500) NULL,
	[OnlyIndex] [bit] NULL,
	[PageId] [int] NULL,
	[ExtendId] [int] NULL,
	[RemType] [varchar](50) NULL,
	[SortType] [varchar](50) NULL,
	[ContentTemplate] [varchar](500) NULL,
	[ContentPath] [varchar](100) NULL,
	[Extension] [varchar](10) NULL,
	[ImageUrl] [varchar](100) NULL,
	[Description] [nvarchar](500) NULL,
	[KeyWords] [nvarchar](500) NULL,
	[NavigateUrl] [varchar](250) NULL,
	[Approval] [int] NULL,
	[Display] [int] NULL,
	[ContentType] [int] NULL,
	[SortOrder] [int] NULL,
 CONSTRAINT [PK_cms_Channels] PRIMARY KEY CLUSTERED 
(
	[ChannelId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[cms_Article]    Script Date: 12/09/2017 11:28:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[cms_Article](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ContentId] [uniqueidentifier] NOT NULL,
	[ParentId] [uniqueidentifier] NULL,
	[UserId] [int] NULL,
	[AdminId] [int] NULL,
	[ChildId] [int] NULL,
	[UserName] [nvarchar](20) NULL,
	[UserIP] [varchar](30) NULL,
	[Dateline] [datetime] NULL,
	[Source] [nvarchar](50) NULL,
	[Author] [nvarchar](50) NULL,
	[ChannelId] [int] NULL,
	[SortId] [int] NULL,
	[Tags] [nvarchar](50) NULL,
	[Subject] [nvarchar](250) NULL,
	[Subhead] [nvarchar](250) NULL,
	[ShortTitle] [nvarchar](50) NULL,
	[TitleImage] [varchar](50) NULL,
	[Description] [nvarchar](200) NULL,
	[Body] [ntext] NULL,
	[Attachfiles] [varchar](200) NULL,
	[HasImage] [bit] NULL,
	[Color] [varchar](20) NULL,
	[TitleStyle] [int] NULL,
	[Recommends] [varchar](100) NULL,
	[SortOrder] [int] NULL,
	[Allowreply] [bit] NULL,
	[PageUrl] [varchar](500) NULL,
	[TotalReply] [int] NULL,
	[TotalView] [int] NULL,
	[TotalPoll] [int] NULL,
	[Poll] [varchar](50) NULL,
	[State] [int] NULL,
	[ApprovedBy] [nvarchar](20) NULL,
 CONSTRAINT [PK_cms_Article] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AT_LM]    Script Date: 12/09/2017 11:28:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AT_LM](
	[LM_ID] [int] NOT NULL,
	[LM_TITLE] [varchar](100) NOT NULL,
	[LM_LINK] [varchar](150) NULL,
	[LM_PARENT] [int] NULL,
	[LM_NUM] [int] NULL,
	[ROOTID] [int] NULL,
	[DEPTH] [int] NULL,
	[CHILDSUM] [int] NULL,
	[LM_MODE] [varchar](100) NULL,
	[LM_NODE] [varchar](250) NULL,
	[ISAUTOCHECK] [char](1) NULL,
	[LMLEVEL] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AT_INFO]    Script Date: 12/09/2017 11:28:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AT_INFO](
	[INFO_ID] [int] NOT NULL,
	[INFO_TITLE] [varchar](200) NOT NULL,
	[INFO_VALUE] [text] NULL,
	[INFO_TIME] [varchar](32) NULL,
	[INFO_TYPE] [int] NULL,
	[INFO_IMAGE] [varchar](20) NULL,
	[INFO_LINK] [varchar](50) NULL,
	[INFO_AUTHOR] [varchar](50) NULL,
	[INFO_EXPLAIN] [varchar](200) NULL,
	[INFO_XG] [varchar](100) NULL,
	[INFO_NUM] [int] NULL,
	[CLICK] [int] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Default [DF__AT_INFO__CLICK__07C12930]    Script Date: 12/09/2017 11:28:44 ******/
ALTER TABLE [dbo].[AT_INFO] ADD  DEFAULT (0) FOR [CLICK]
GO
/****** Object:  Default [DF__AT_LM__LMLEVEL__0A9D95DB]    Script Date: 12/09/2017 11:28:44 ******/
ALTER TABLE [dbo].[AT_LM] ADD  DEFAULT (1) FOR [LMLEVEL]
GO
