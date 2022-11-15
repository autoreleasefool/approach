# frozen_string_literal: true

require 'set'
require_relative './dependency'

module Target
  def self.fetch_targets(package_yaml)
    targets = {}

    target_types = %w[feature data_provider service library]

    target_types.each do |type|
      package_yaml['package'][type].each do |t|
        name = t['name']
        target = Target.new(name: name, type: type, is_product: true, requires_tests: t['requires_tests'])
        targets[name] = target

        interface_target = target.interface
        if interface_target
          target.add_dependency(interface_target)
          targets[interface_target.name] = interface_target
        end

        test_target = target.test
        if test_target
          test_target.add_dependency(target)
          targets[test_target.name] = test_target
        end
      end
    end
    targets
  end

  class Target < Dependency::Dependency
    attr_reader :name, :type, :base, :dependencies

    @@target_types = %w[
      test
      feature
      data_provider
      data_provider_interface
      service
      service_interface
      library
    ]

    def initialize(name:, type:, base: nil, is_product:, requires_tests:)
      @name = name
      @dependencies = Set.new
      @type = type
      @base = base || type
      @is_product = is_product
      @requires_tests = requires_tests || %w[feature data_provider service].include?(@type)

      raise "Unsupported target type #{@type} for #{@name}" unless @@target_types.include?(@type)
    end

    def add_dependency(dep)
      raise "#{@name} does not support #{dep.type}" unless dep.external? ||
                                                           supported_dependency_types.include?(dep.type)

      @dependencies << dep.name
    end

    def add_transient_dependency(dep)
      count = @dependencies.count
      add_dependency(dep)

      raise "#{@name} has transient dependency #{dep.name}" if @dependencies.count > count
    end

    def remove_dependency(dep)
      @dependencies.delete(dep.name)
    end

    def dependencies_include?(dep)
      @dependencies.include?(dep.name)
    end

    def interface
      return unless interface_name

      Target.new(name: interface_name, type: "#{@type}_interface", base: @type, is_product: true, requires_tests: false)
    end

    def test
      return unless test_name

      Target.new(name: test_name, type: 'test', base: @type, is_product:false, requires_tests: false)
    end

    def interface?
      @type.end_with?('interface')
    end

    def external?
      false
    end

    def product?
      @is_product
    end

    private

    def supported_dependency_types
      case @type
      when 'feature' then %w[feature data_provider_interface service_interface library]
      when 'data_provider', 'data_provider_interface' then %w[data_provider_interface service_interface library]
      when 'service', 'service_interface' then %w[service_interface library]
      when 'library' then ['library']
      when 'test' then base
      end
    end

    def interface_name
      case @type
      when 'data_provider', 'service'
        "#{@name}Interface"
      end
    end

    def test_name
      "#{name}Tests" if @requires_tests
    end
  end
end
