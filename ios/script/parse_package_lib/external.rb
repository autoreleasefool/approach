# frozen_string_literal: true

require_relative './dependency'

module External
  def self.fetch_exports(package_yaml)
    exports = {}
    package_yaml['package']['dependencies'].flat_map do |d|
      d['exports'].map do |e|
        exports[e] = Export.new(name: e, package: d['url'][%r{https://github\.com/.*?/(.*)\.git}, 1])
        exports[exports[e].name] = exports[e]
      end
    end
    exports
  end

  def self.fetch_packages(package_yaml)
    packages = {}
    package_yaml['package']['dependencies'].each do |d|
      name = d['url'][%r{https://github\.com/.*?/(.*)\.git}, 1]
      if d.include?('from')
        packages[name] = Package.new(url: d['url'], from: d['from'])
      elsif d.include?('branch')
        packages[name] = Package.new(url: d['url'], branch: d['branch'])
      elsif d.include?('revision')
        packages[name] = Package.new(url: d['url'], revision: d['revision'])
      end
    end

    packages
  end

  class Package
    attr_reader :url, :from, :branch, :revision

    def initialize(url:, from: nil, branch: nil, revision: nil)
      @url = url
      @from = from
      @branch = branch
      @revision = revision
    end
  end

  class Export < Dependency::Dependency
    attr_reader :name

    def initialize(name:, package:)
      @name = ".product(name: \"#{name}\", package: \"#{package}\")"
    end

    def external?
      true
    end

    def interface
      nil
    end
  end
end
