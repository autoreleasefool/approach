# frozen_string_literal: true

module Package
  class Package
    attr_reader :name, :platforms, :tools_version

    def initialize(name:, platforms:, tools_version:)
      @name = name
      @platforms = platforms
      @tools_version = tools_version
    end
  end
end
