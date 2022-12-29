# frozen_string_literal: true

module Package
  class Package
    attr_reader :name, :platforms, :default_localization, :tools_version

    def initialize(name:, platforms:, default_localization:, tools_version:)
      @name = name
      @platforms = platforms
      @default_localization = default_localization
      @tools_version = tools_version
    end
  end
end
